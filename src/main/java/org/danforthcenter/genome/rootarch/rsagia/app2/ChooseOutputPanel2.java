/*
 *  Copyright 2012 vp23.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.danforthcenter.genome.rootarch.rsagia2.ApplicationManager;
import org.danforthcenter.genome.rootarch.rsagia2.FileUtil;
import org.danforthcenter.genome.rootarch.rsagia2.OutputInfo;
import org.danforthcenter.genome.rootarch.rsagia2.RsaImageSet;

/**
 *
 * @author vp23
 *
 *         This class is now used instead of ChooseOutputPanel (provides a
 *         better layout compared with ChooseOutputPanel)
 *
 */
public class ChooseOutputPanel2 extends javax.swing.JPanel implements
		java.awt.event.ActionListener {

	protected HashMap<RsaImageSet, ArrayList<OutputInfo>> originalMap;
	protected boolean oneOutputOnly;

	// ============================<editor-fold desc="Variables declaration">{{{
	// ChooseOutputPanel2 - border layout
	private javax.swing.JPanel jPanelN;
	private javax.swing.JButton toFinalButton;
	private javax.swing.JButton toReviewButton;
	private javax.swing.JPanel jPanelC;
	private javax.swing.JSplitPane jSplitter;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTree reviewTree;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JList<OutputInfo> finalList;

	// End of variables declaration...........................}}}</editor-fold>

	/** Creates new form ChooseOutputPanel2 */
	public ChooseOutputPanel2(HashMap<RsaImageSet, ArrayList<OutputInfo>> map,
			boolean oneOutputOnly, ApplicationManager am, boolean moveSingles) {
		initPanel();
		this.reviewTree.setCellRenderer(new ReviewTreeCellRenderer(am));
		this.reviewTree.setRootVisible(false);

		reviewTree.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					handleReviewDoubleClick(e);
				}
			}
		});

		originalMap = map;
		finalList.setModel(new DefaultListModel<>());
		finalList.setCellRenderer(new FinalListCellRenderer(am));

		toFinalButton.addActionListener(this);
		toReviewButton.addActionListener(this);
		this.oneOutputOnly = oneOutputOnly;

		for (Map.Entry<RsaImageSet, ArrayList<OutputInfo>> ent : originalMap
				.entrySet()) {
			if (moveSingles && ent.getValue().size() == 1) {
				DefaultListModel<OutputInfo> dlm = (DefaultListModel<OutputInfo>) finalList.getModel();
				dlm.addElement(ent.getValue().get(0));
			} else {
				addToReview(ent.getKey(), ent.getValue());
			}
		}
	}

	/**
	 * When a user double clicks on an element in the review tree, open a file
	 * browser to that OutputInfo's directory for review.
	 * 
	 * @param e
	 */
	public void handleReviewDoubleClick(MouseEvent e) {
		if (!reviewTree.isSelectionEmpty()) {
			TreePath tp = reviewTree.getSelectionPath();
			if (tp.getPathCount() == 3) {
				DefaultMutableTreeNode n = (DefaultMutableTreeNode) tp
						.getLastPathComponent();
				OutputInfo oi = (OutputInfo) n.getUserObject();
				FileUtil.openFileBrowser(oi.getDir());
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(toFinalButton)) {
			sendSelectedReviewToFinal();
		} else if (e.getSource().equals(toReviewButton)) {
			sendSelectedFinalToReview();
		}
	}

	public void removeFinalOutputs() {
		DefaultListModel dlm = (DefaultListModel) finalList.getModel();
		while (dlm.getSize() > 0) {
			OutputInfo oi = (OutputInfo) dlm.get(0);
			if (oneOutputOnly) {
				originalMap.remove(oi.getRis());
			} else {
				originalMap.get(oi.getRis()).remove(oi);
			}
			dlm.remove(0);
		}
	}

	protected void sendSelectedFinalToReview() {
		DefaultListModel dlm = (DefaultListModel) finalList.getModel();
		int[] inds = finalList.getSelectedIndices();

		if (inds != null) {
			for (int j = inds.length - 1; j >= 0; j--) {
				int ind = inds[j];
				OutputInfo oi = (OutputInfo) dlm.get(ind);
				RsaImageSet r = oi.getRis();

				// if we are allowed more than one output, then we should just
				// return
				// this selected output
				if (!oneOutputOnly) {
					ArrayList<OutputInfo> arr = new ArrayList<OutputInfo>();
					arr.add(oi);
					addToReview(r, arr);
				} else // we should put them all back as options
				{
					addToReview(r, originalMap.get(r));
				}

				dlm.remove(ind);
			}
		}
	}

	protected void sendSelectedReviewToFinal() {
		TreePath[] tps = reviewTree.getSelectionPaths();
		if (tps != null) {
			DefaultTreeModel dtm = (DefaultTreeModel) reviewTree.getModel();
			for (TreePath tp : tps) {
				if (tp.getPathCount() == 3) {
					DefaultMutableTreeNode n = (DefaultMutableTreeNode) tp
							.getLastPathComponent();
					DefaultMutableTreeNode p = (DefaultMutableTreeNode) n
							.getParent();
					int i = p.getIndex(n);
					n.removeFromParent();
					dtm.nodesWereRemoved(p, new int[] { i }, new Object[] { n });

					if (p.getChildCount() == 0 || oneOutputOnly) {
						DefaultMutableTreeNode p2 = (DefaultMutableTreeNode) p
								.getParent();
						int i2 = p2.getIndex(p);
						p.removeFromParent();
						dtm.nodesWereRemoved(p2, new int[] { i2 },
								new Object[] { p });
					}

					DefaultListModel<OutputInfo> dlm = (DefaultListModel<OutputInfo>) finalList
							.getModel();
					dlm.addElement((OutputInfo) n.getUserObject());
				}
			}
		}
	}

	public ArrayList<OutputInfo> getFinalOutputs() {
		ArrayList<OutputInfo> ans = new ArrayList<OutputInfo>();
		for (int i = 0; i < finalList.getModel().getSize(); i++) {
			ans.add((OutputInfo) finalList.getModel().getElementAt(i));
		}

		return ans;
	}

	protected void addToReview(RsaImageSet ris, ArrayList<OutputInfo> infos) {
		DefaultTreeModel dtm = (DefaultTreeModel) this.reviewTree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) dtm.getRoot();

		// first try and find the node
		// we go last to back because we most likely just added this
		DefaultMutableTreeNode rn = null;
		for (int i = root.getChildCount() - 1; i >= 0; i--) {
			DefaultMutableTreeNode n = (DefaultMutableTreeNode) root
					.getChildAt(i);

			// we're dealing with references still
			if (n.getUserObject() == ris) {
				rn = n;
			}
		}

		// not found, create a new one
		if (rn == null) {
			rn = new DefaultMutableTreeNode(ris);
			dtm.insertNodeInto(rn, root, root.getChildCount());
		}

		DefaultMutableTreeNode toExpand = rn;
		for (OutputInfo oi : infos) {
			toExpand = new DefaultMutableTreeNode(oi);
			dtm.insertNodeInto(toExpand, rn, rn.getChildCount());
		}
		TreePath tp = new TreePath(new Object[] { root, rn });

		this.reviewTree.expandPath(tp);
	}

	protected static String formatRsaImageSet(RsaImageSet ris) {
		return ris.getExperiment() + "." + ris.getSpecies() + "."
				+ ris.getPlant() + "." + ris.getImagingDay();
	}

	protected static String formatOutputInfo(OutputInfo oi,
			ApplicationManager am) {
		return (oi.isSaved() ? "(saved) " : "(sandbox) ")
				+ am.getApplicationByName(oi.getAppName()).getReviewString(oi);
	}

	protected static class FinalListCellRenderer extends
			javax.swing.DefaultListCellRenderer {
		protected ApplicationManager am;

		public FinalListCellRenderer(ApplicationManager am) {
			super();
			this.am = am;
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);

			if (OutputInfo.class.isAssignableFrom(value.getClass())) {
				OutputInfo oi = (OutputInfo) value;
				if (oi.isValid()) {
					setText(formatRsaImageSet(oi.getRis()) + " "
							+ formatOutputInfo(oi, am));
				} else {
					setText(oi.getDir().getAbsolutePath());
					setForeground(Color.red);
				}
			}

			return this;
		}

	}

	protected static class ReviewTreeCellRenderer extends
			javax.swing.tree.DefaultTreeCellRenderer {
		protected ApplicationManager am;

		public ReviewTreeCellRenderer(ApplicationManager am) {
			super();
			this.am = am;
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			Component ans = super.getTreeCellRendererComponent(tree, value,
					sel, expanded, leaf, row, hasFocus);
			DefaultMutableTreeNode n = (DefaultMutableTreeNode) value;
			Object obj = n.getUserObject();
			if (obj.getClass().equals(RsaImageSet.class)) {
				setText(ChooseOutputPanel2.formatRsaImageSet((RsaImageSet) obj));
			} else if (OutputInfo.class.isAssignableFrom(obj.getClass())) {
				OutputInfo oi = (OutputInfo) obj;
				if (oi.isValid()) {
					setText(formatOutputInfo(oi, am));
				} else {
					setText(oi.getDir().getAbsolutePath());
					// this.setTextNonSelectionColor(Color.red);
					// this.setBackground(Color.red);
					// ans.setBackground(Color.red);
					ans.setForeground(Color.red);// .setTextNonSelectionColor(Color.red);
				}

				// this.setText(ChooseOutputPanel2.formatOutputInfo((OutputInfo)obj,am));
			}

			return ans;
		}
	}

	void initPanel() {
		jPanelN = new JPanel(new BorderLayout());
		toFinalButton = new javax.swing.JButton();
		toFinalButton.setText(">>");
		toReviewButton = new javax.swing.JButton();
		toReviewButton.setText("<<");
		jPanelN.add(toFinalButton, BorderLayout.WEST);
		jPanelN.add(toReviewButton, BorderLayout.EAST);

		jPanelC = new JPanel(new BorderLayout());
		reviewTree = new javax.swing.JTree();
		javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode(
				"root");
		reviewTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
		reviewTree.setRootVisible(false);
		jScrollPane1 = new javax.swing.JScrollPane(reviewTree);
		finalList = new javax.swing.JList<>();
		jScrollPane2 = new javax.swing.JScrollPane(finalList);

		jSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false,
				jScrollPane1, jScrollPane2);
		jSplitter.setOneTouchExpandable(true);
		jSplitter.setResizeWeight(0.5);
		jPanelC.add(jSplitter, BorderLayout.CENTER);

		this.setLayout(new BorderLayout());
		this.add(jPanelN, BorderLayout.NORTH);
		this.add(jPanelC, BorderLayout.CENTER);
	}
}
