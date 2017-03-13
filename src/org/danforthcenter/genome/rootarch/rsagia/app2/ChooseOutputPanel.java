/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ChooseOutputPanel.java
 *
 * Created on Jul 22, 2010, 2:26:32 PM
 */

package org.danforthcenter.genome.rootarch.rsagia.app2;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.danforthcenter.genome.rootarch.rsagia2.ApplicationManager;
import org.danforthcenter.genome.rootarch.rsagia2.FileUtil;
import org.danforthcenter.genome.rootarch.rsagia2.OutputInfo;
import org.danforthcenter.genome.rootarch.rsagia2.RsaImageSet;

/**
 *
 * @author bm93
 *
 *         vp23 - now this class is obsolete, not used. See ChooseOutputPanel2
 *         Also see ReviewFrame2.
 *
 */
public class ChooseOutputPanel extends javax.swing.JPanel implements
		java.awt.event.ActionListener {

	protected HashMap<RsaImageSet, ArrayList<OutputInfo>> originalMap;
	protected boolean oneOutputOnly;

	/** Creates new form ChooseOutputPanel */
	public ChooseOutputPanel(HashMap<RsaImageSet, ArrayList<OutputInfo>> map,
			boolean oneOutputOnly, ApplicationManager am, boolean moveSingles) {
		initComponents();
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
		finalList.setModel(new DefaultListModel());
		finalList.setCellRenderer(new FinalListCellRenderer(am));

		toFinalButton.addActionListener(this);
		toReviewButton.addActionListener(this);
		this.oneOutputOnly = oneOutputOnly;

		for (Map.Entry<RsaImageSet, ArrayList<OutputInfo>> ent : originalMap
				.entrySet()) {
			if (moveSingles && ent.getValue().size() == 1) {
				DefaultListModel dlm = (DefaultListModel) finalList.getModel();
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

					DefaultListModel dlm = (DefaultListModel) finalList
							.getModel();
					dlm.addElement(n.getUserObject());
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
				setText(ChooseOutputPanel.formatRsaImageSet((RsaImageSet) obj));
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

				// this.setText(ChooseOutputPanel.formatOutputInfo((OutputInfo)obj,am));
			}

			return ans;
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jScrollPane1 = new javax.swing.JScrollPane();
		reviewTree = new javax.swing.JTree();
		jScrollPane2 = new javax.swing.JScrollPane();
		finalList = new javax.swing.JList();
		toFinalButton = new javax.swing.JButton();
		toReviewButton = new javax.swing.JButton();

		javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode(
				"root");
		reviewTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
		reviewTree.setRootVisible(false);
		jScrollPane1.setViewportView(reviewTree);

		jScrollPane2.setViewportView(finalList);

		toFinalButton.setText(">>");

		toReviewButton.setText("<<");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addComponent(jScrollPane1,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										315,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.TRAILING)
												.addComponent(toFinalButton)
												.addComponent(toReviewButton))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(jScrollPane2,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										322, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup().addGap(81, 81, 81)
								.addComponent(toFinalButton).addGap(30, 30, 30)
								.addComponent(toReviewButton)
								.addContainerGap(114, Short.MAX_VALUE))
				.addComponent(jScrollPane1,
						javax.swing.GroupLayout.DEFAULT_SIZE, 283,
						Short.MAX_VALUE)
				.addComponent(jScrollPane2,
						javax.swing.GroupLayout.DEFAULT_SIZE, 283,
						Short.MAX_VALUE));
	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JList finalList;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JTree reviewTree;
	private javax.swing.JButton toFinalButton;
	private javax.swing.JButton toReviewButton;
	// End of variables declaration//GEN-END:variables

}
