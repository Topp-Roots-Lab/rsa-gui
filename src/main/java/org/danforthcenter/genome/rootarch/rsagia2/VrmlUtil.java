/*
 *  Copyright 2013 vp23.
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

package org.danforthcenter.genome.rootarch.rsagia2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// tw 2014july24
import java.io.FilenameFilter;

/**
 * 
 * @author vp23
 * 
 */
@SuppressWarnings("unchecked")
public class VrmlUtil {

	// ============================<editor-fold desc="main">{{{
	public static void main(String[] args) {
		// createShapeVrmlForVoxelsFiles(new File(args[0]));

		// test
		// createShapeVrmlForVoxelsFiles(new File("dummy"));
		createShapeVrmlForVoxelsFiles(new File("/home/twalk/roots3d/MsERFp0030d13_cropped"));

		// test 2
		// createShapeVrmlForVoxelsFile(new File("dummy"));
		// createShapeVrmlForVoxelsFile(new
		// File("/home/twalk/recon_test_set/"));
	}

	// End of main...........................}}}</editor-fold>

	// ============================<editor-fold desc="variables">{{{

	// End of variables...........................}}}</editor-fold>

	// ========================<editor-fold
	// desc="createShapeVrmlForVoxelsFiles">{{{
	public static void createShapeVrmlForVoxelsFile(File path) {

		try {
			//
			// test
			//
			// biobusch
			// path = new
			// File("/localhome/vp23/tmp/voxels/insilico/rightangle-1x1_rightangle-3x3.out");
			// tablet
			// path = new
			// File("/localhome/vp23/Data/voxels/insilico/rightangle-1x1_rightangle-3x3.out");

			//
			// get components data and info
			//
			ArrayList data = getComponentData(path);
			ArrayList compdata = getComponentsData(path);
			// HashMap<Integer,ArrayList<Pnt3d_i>> cmps_srt =
			// (HashMap<Integer,ArrayList<Pnt3d_i>>)compdata.get(0);
			ArrayList<ArrayList<Pnt3d_i>> cmps_srt = (ArrayList<ArrayList<Pnt3d_i>>) compdata
					.get(0);
			Pnt3d_i size = (Pnt3d_i) data.get(1);
			File main_vxs_full_name = (File) data.get(2);

			//
			// VRML
			//
			StringBuilder vrml = createVrml(cmps_srt, size);

			//
			// save to file
			//
			try {

				String fn = main_vxs_full_name.getName();
				String dir = main_vxs_full_name.getParent();

				File out = new File(dir + File.separator + fn + "-"
						+ "coloredregions" + ".wrl");
				saveToFile(out, vrml.toString());
			} catch (Exception exp) {
				System.out.println(exp.getMessage());
			}
		} catch (Exception exp) {
			String ErrMsg = "Unexpected error:" + exp.getMessage();
			throw new VrmlUtilException(ErrMsg, exp);
		}

	}

	// End of
	// createShapeVrmlForVoxelsFile........................}}}</editor-fold>

	// ========================<editor-fold
	// desc="createShapeVrmlForVoxelsFiles">{{{
	public static void createShapeVrmlForVoxelsFiles(File path) {

		try {
			//
			// test
			//
			// path = new File("/localhome/vp23/Data/sf98_2011-04-29_14-58-58");
			// path = new
			// File("/localhome/vp23/tmp/voxels/insilico/components");
			// tablet
			// path = new
			// File("/localhome/vp23/Data/voxels/insilico/components");

			// tw 2014july24
			System.out.println(path);

			//
			// get components data and info
			//
			ArrayList compdata = getComponentsData(path);
			// HashMap<Integer,ArrayList<Pnt3d_i>> cmps_srt =
			// (HashMap<Integer,ArrayList<Pnt3d_i>>)compdata.get(0);
			ArrayList<ArrayList<Pnt3d_i>> cmps_srt = (ArrayList<ArrayList<Pnt3d_i>>) compdata
					.get(0);
			Pnt3d_i size = (Pnt3d_i) compdata.get(1);
			File main_vxs_full_name = (File) compdata.get(2);

			//
			// VRML
			//
			StringBuilder vrml = createVrml(cmps_srt, size);

			//
			// save to file
			//
			try {

				String fn = main_vxs_full_name.getName();
				String dir = main_vxs_full_name.getParent();

				// File out = new File(dir+File.separator+fn+"-"+
				// "coloredregions"+
				// ".wrl");
				File out = new File(dir + File.separator + fn + ".wrl");
				saveToFile(out, vrml.toString());
			} catch (Exception exp) {
				System.out.println(exp.getMessage());
			}
		} catch (Exception exp) {
			String ErrMsg = "Unexpected error:" + exp.getMessage() + " --- "
					+ exp.getCause().getMessage();
			throw new VrmlUtilException(ErrMsg, exp);
		}

	}

	// End of
	// createShapeVrmlForVoxelsFiles........................}}}</editor-fold>

	// ============================<editor-fold desc="getComponentData">{{{
	private static ArrayList getComponentData(File path) {
		ArrayList ret = new ArrayList();

		HashMap<Integer, ArrayList<Pnt3d_i>> cmps = new HashMap();
		HashMap<Integer, ArrayList<Pnt3d_i>> cmps_srt = new HashMap();
		HashMap<Integer, Integer> cmps_sz = new HashMap();

		// get size of the whole root
		Pnt3d_i size = (Pnt3d_i) getVoxels(path).get(1);
		File main_vxs_full_name = path.getAbsoluteFile();

		// get voxels for the given file
		ArrayList<Pnt3d_i> vxs = (ArrayList<Pnt3d_i>) getVoxels(path).get(0);

		// only one component - one voxels file
		int num = 1;

		cmps.put(num, vxs);
		cmps_sz.put(num, vxs.size());

		LinkedHashMap<Integer, Integer> cmp_size_srt = sortHashMapByValD(cmps_sz);

		Object[] nums = cmp_size_srt.keySet().toArray();
		for (int i = 0; i < nums.length; i++) {
			Integer num_new = (Integer) nums[i];
			cmps_srt.put(num_new, (ArrayList<Pnt3d_i>) cmps.get(num_new));
		}

		ret.add(cmps_srt);
		ret.add(size);
		ret.add(main_vxs_full_name);

		return ret;

	}

	// End of getComponentData...........................}}}</editor-fold>

	// ============================<editor-fold desc="getComponentsData">{{{
	private static ArrayList getComponentsData(File path) {
		ArrayList ret = new ArrayList();

		HashMap<Integer, ArrayList<Pnt3d_i>> cmps = new HashMap();
		// HashMap<Integer,ArrayList<Pnt3d_i>> cmps_srt = new HashMap();
		ArrayList<ArrayList<Pnt3d_i>> cmps_srt = new ArrayList();
		HashMap<Integer, Integer> cmps_sz = new HashMap();

		// get size of the whole root (with all components in it)
		Pnt3d_i size = null;
		File main_vxs_full_name = null;
		MainVoxelsFileFilter mvff = new MainVoxelsFileFilter();

		// tw 2014july24
		System.out.println("mvff.getFilter " + mvff.getFilter());
		System.out.println("mvff " + mvff);
		System.out.println("path " + path);

		File f1 = new File(path.toString());
		System.out.println("f1 " + f1);

		FilenameFilter mvff1 = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".out")) {
					return true;
				} else {
					return false;
				}
			}
		};

		// File[] fs = path.listFiles(mvff);

		// File[] fs = new File[2];
		// File[] fs = new File[path.listFiles(mvff).length];
		File[] fs = f1.listFiles(mvff1);

		// File[] fs = path.listFiles(mvff1);

		// tw 2014july24
		System.out.println("files " + fs.length);

		for (File f : fs) {

			// tw 2014july24
			System.out.println("file " + f.getName());

			main_vxs_full_name = f.getAbsoluteFile();
			// get size only
			size = (Pnt3d_i) getVoxels(f).get(1);
		}

		// get voxels for the all cmoponents
		ComponentsFileFilter cff = new ComponentsFileFilter();
		fs = path.listFiles(cff);
		if (fs.length > 1) {
			for (File f : fs) {
				ArrayList<Pnt3d_i> vxs = (ArrayList<Pnt3d_i>) getVoxels(f).get(
						0);

				int num = getComponentNumber(f, cff);

				cmps.put(num, vxs);
				cmps_sz.put(num, vxs.size());
			}
		} else {
			ArrayList<Pnt3d_i> vxs = (ArrayList<Pnt3d_i>) getVoxels(
					main_vxs_full_name).get(0);

			int num = 1;

			cmps.put(num, vxs);
			cmps_sz.put(num, vxs.size());
		}

		// LinkedHashMap<Integer,Integer>
		// cmp_size_srt=sortHashMapByValD(cmps_sz);
		Map<Integer, Integer> cmp_size_srt = MapUtil.sortByValue(cmps_sz);

		Object[] keys = cmp_size_srt.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			// Integer num_new = (Integer)keys[i];
			// cmps_srt.a(num_new, (ArrayList<Pnt3d_i>)cmps.get(num_new));

			Integer num_new = (Integer) keys[i];
			cmps_srt.add((ArrayList<Pnt3d_i>) cmps.get(num_new));
		}

		ret.add(cmps_srt);
		ret.add(size);
		ret.add(main_vxs_full_name);

		return ret;

	}

	// End of getComponentsData...........................}}}</editor-fold>

	// ============================<editor-fold desc="createVrml">{{{
	// private static StringBuilder
	// createVrml(HashMap<Integer,ArrayList<Pnt3d_i>> cmps_srt,
	private static StringBuilder createVrml(
			ArrayList<ArrayList<Pnt3d_i>> cmps_srt, Pnt3d_i size) {
		StringBuilder vrml = new StringBuilder();

		// add VRML header
		vrml.append("#VRML V2.0 utf8\n");

		// initialize colors
		double red = 0.0;
		double green = 0.0;
		double blue = 0.0;
		int cnt = 1;

		// TODO: don't use "if" in the loop
		// Object[] ks = cmps_srt.keySet().toArray();
		// Object[] vs = cmps_srt.values().toArray();
		ArrayList<Pnt3d_i> vxs = null;
		for (int i = 0; i < cmps_srt.size(); i++) {
			// Integer num = (Integer) ks[i];
			// vxs = (ArrayList<Pnt3d_i>)cmps_srt.get(num);

			vxs = (ArrayList<Pnt3d_i>) cmps_srt.get(i);

			if (i == cmps_srt.size() - 1) {
				// the biggest region
				// red = 0.0;
				// green = 1.0;
				// blue = 0.0;
				red = 0.75;
				green = 0.35;
				blue = 0.01;
			}
			;

			// for two components, if there more than one
			// the size of the second biggest component
			int vxs2 = -1;
			if (cmps_srt.size() > 1 && i == cmps_srt.size() - 2) {
				// the size of the second biggest component
				vxs2 = ((ArrayList<Pnt3d_i>) cmps_srt.get(cmps_srt.size() - 2))
						.size();
				// the second biggest region
				red = 0.0;
				green = 0.0;
				blue = 1.0;
			}
			;

			// color leftovers

			if (i < cmps_srt.size() - 2) {
				if (vxs.size() >= vxs2 * 4 / 5 && vxs.size() < vxs2) {
					red = 0.2;
					green = 0.0;
					blue = 0.0;
				} else if (vxs.size() >= vxs2 * 3 / 5
						&& vxs.size() < vxs2 * 4 / 5) {
					red = 0.4;
					green = 0.0;
					blue = 0.0;
				} else if (vxs.size() >= vxs2 * 2 / 5
						&& vxs.size() < vxs2 * 3 / 5) {
					red = 0.6;
					green = 0.0;
					blue = 0.0;
				} else if (vxs.size() >= vxs2 * 1 / 5
						&& vxs.size() < vxs2 * 2 / 5) {
					red = 0.8;
					green = 0.0;
					blue = 0.0;
				} else if (vxs.size() <= vxs2 * 1 / 5) {
					red = 1.0;
					green = 0.0;
					blue = 0.0;
				}
			}
			VrmlUtil.createShapeVrml(vrml, vxs, size, red, green, blue);
			cnt++;
		}

		return vrml;
	};

	// End of createVrml...........................}}}</editor-fold>

	// ============================<editor-fold desc="createShapeVrml">{{{
	private static void createShapeVrml(StringBuilder vrml,
			ArrayList<Pnt3d_i> voxels, Pnt3d_i size, double red, double green,
			double blue) {

		// ArrayList<Pnt3d_i> voxels = rgn.getPoints();

		//
		// make and get the vrml coordinates and indices
		//
		ArrayList crds_inds = getCoordsAndIndicesVrml(voxels, size);
		// get crds_srt and inds
		LinkedHashMap<Integer, Integer> crds_srt = (LinkedHashMap) crds_inds
				.get(0);
		ArrayList<ArrayList<Integer>> inds = (ArrayList) crds_inds.get(1);

		//
		// make and get VRML
		//
		makeShapeVrml(size, vrml, red, green, blue, crds_srt, inds);

		// will return vrml by ref

	}

	// End of createShapeVrml...........................}}}</editor-fold>

	//
	// Obsolete - used only in the ProcessRegion3d
	//
	// ============================<editor-fold desc="createShapeVrml">{{{
	// public static void createShapeVrml( StringBuilder vrml,
	// BinaryRegion rgn,
	// Integer rgn_id,
	// Pnt3d_i size,
	// double red,
	// double green,
	// double blue,
	// String path){
	//
	// ArrayList<Pnt3d_i> voxels = rgn.getPoints();
	//
	// //////////////////////////////////////////////////////////////////////////
	// //// ouput voxels for regions
	// //////////////////////////////////////////////////////////////////////////
	// // StringBuilder vs = new StringBuilder();
	// // String vsline = "";
	// // vs.append("0.15\n");// extra info - this data not used
	// // vs.append(voxels.size()+"\n");// number of voxels - this data not used
	// // for(int i=0;i<voxels.size();i++){
	// // Pnt3d_i p = voxels.get(i);
	// // vsline = String.valueOf(p.x)+" "+
	// // String.valueOf(p.y)+" "+
	// // String.valueOf(p.z)+"\n";
	// // vs.append(vsline);
	// // }
	// // // save to file
	// // try{
	// //
	// // String fn = new File(path).getName();
	// // String dir = new File(path).getParent();
	// //
	// // File out = new File(dir+File.separator+fn+"-"+
	// // "region"+"-"+ String.valueOf(rgn)+
	// // ".out");
	// // saveToFile(out,vs.toString());
	// // }catch(Exception exp){
	// // System.out.println(exp.getMessage());
	// // }
	// //////////////////////////////////////////////////////////////////////////
	//
	// //
	// // make and get the vrml coordinates and indices
	// //
	// ArrayList crds_inds = getCoordsAndIndicesVrml(voxels,size);
	// // get crds_srt and inds
	// LinkedHashMap<Integer,Integer> crds_srt =
	// (LinkedHashMap)crds_inds.get(0);
	// ArrayList<ArrayList<Integer>> inds = (ArrayList)crds_inds.get(1);
	//
	// //
	// // make and get VRML
	// //
	// makeShapeVrml(size,vrml,red,green,blue,crds_srt,inds);
	//
	// // will return vrml by ref
	//
	// }
	// End of createShapeVrml...........................}}}</editor-fold>

	// //========================<editor-fold desc="getCoordsAndIndicesVrml">{{{
	// private static ArrayList getCoordsAndIndicesVrml(ArrayList<Pnt3d_i>
	// voxels,
	// Pnt3d_i size){
	// //
	// // intialiaze return values
	// //
	// ArrayList ret = new ArrayList();
	// ArrayList<ArrayList<Integer>> inds = new ArrayList();
	// LinkedHashMap<Integer,Integer> crds_srt = new LinkedHashMap();
	//
	// int sx = size.x;
	// int sy = size.y;
	// int sz = size.z;
	// //
	// // intialiaze
	// //
	// HashMap<Integer,Pnt3d_i> linvoxels = new HashMap();
	// HashMap<Integer,Integer> crds = new HashMap();
	// ArrayList<Integer> ind = null;
	//
	// //these are vertices of a voxel
	// Integer v1,v2,v3,v4,v5,v6,v7,v8;
	// //indices per square face
	// Integer ind1=-1,ind2=-1,ind3=-1,ind4=-1;
	//
	// // creat voxels container with linear indices
	// for(int w=0; w<voxels.size();w++){
	// Pnt3d_i point = voxels.get(w);
	// int linindex = indx2lindx(point,size);
	// linvoxels.put(linindex, point);
	// }
	//
	// int num=0;
	// for(int w=0; w<voxels.size();w++){
	// Pnt3d_i p1 = voxels.get(w);
	// v1=indx2lindx(p1,size);
	//
	// int i = p1.x;
	// int j = p1.y;
	// int k = p1.z;
	//
	// v2=indx2lindx(i+1,j ,k ,size);
	// v3=indx2lindx(i+1,j ,k+1,size);
	// v4=indx2lindx(i ,j ,k+1,size);
	// v5=indx2lindx(i ,j+1,k ,size);
	// v6=indx2lindx(i+1,j+1,k ,size);
	// v7=indx2lindx(i+1,j+1,k+1,size);
	// v8=indx2lindx(i ,j+1,k+1,size);
	//
	// // k
	// //
	// // |
	// // |
	// // |
	// // |
	// // |
	// // |
	// // ------------------------ j
	// // /
	// // /
	// // /
	// // /
	// // /
	// //
	// // i
	// //
	//
	// // the paths of the indices induce normals
	// // with outside direction
	//
	//
	// //left face is visible
	// boolean find = linvoxels.containsKey(v1-sx);
	// if(!find)
	// {
	//
	// if(crds.containsKey(v1)){
	// ind1=crds.get(v1);
	// }else{
	// ind1=num++;
	// crds.put(v1,ind1);
	// }
	//
	// if(crds.containsKey(v2)){
	// ind2=crds.get(v2);
	// }else{
	// ind2=num++;
	// crds.put(v2,ind2);
	// }
	//
	// if(crds.containsKey(v3)){
	// ind3=crds.get(v3);
	// }else{
	// ind3=num++;
	// crds.put(v3,ind3);
	// }
	//
	// if(crds.containsKey(v4)){
	// ind4=crds.get(v4);
	// }else{
	// ind4=num++;
	// crds.put(v4,ind4);
	// }
	//
	// ind = new ArrayList();
	// ind.add(ind1);
	// ind.add(ind2);
	// ind.add(ind3);
	// inds.add(ind);
	// ind = new ArrayList();
	// ind.add(ind1);
	// ind.add(ind3);
	// ind.add(ind4);
	// inds.add(ind);
	// }
	// //right face is visible
	// find = linvoxels.containsKey(v1+sx);
	// if(!find)
	// {
	// if(crds.containsKey(v5)){
	// ind1=crds.get(v5);
	// }else{
	// ind1=num++;
	// crds.put(v5,ind1);
	// }
	//
	// if(crds.containsKey(v6)){
	// ind2=crds.get(v6);
	// }else{
	// ind2=num++;
	// crds.put(v6,ind2);
	// }
	//
	// if(crds.containsKey(v7)){
	// ind3=crds.get(v7);
	// }else{
	// ind3=num++;
	// crds.put(v7,ind3);
	// }
	//
	// if(crds.containsKey(v8)){
	// ind4=crds.get(v8);
	// }else{
	// ind4=num++;
	// crds.put(v8,ind4);
	// }
	//
	// ind = new ArrayList();
	// ind.add(ind1);
	// ind.add(ind4);
	// ind.add(ind3);
	// inds.add(ind);
	// ind = new ArrayList();
	// ind.add(ind1);
	// ind.add(ind3);
	// ind.add(ind2);
	// inds.add(ind);
	// }
	// //front face is visible
	// find = linvoxels.containsKey(v1+1);
	// if(!find)
	// {
	// if(crds.containsKey(v2)){
	// ind1=crds.get(v2);
	// }else{
	// ind1=num++;
	// crds.put(v2,ind1);
	// }
	//
	// if(crds.containsKey(v3)){
	// ind2=crds.get(v3);
	// }else{
	// ind2=num++;
	// crds.put(v3,ind2);
	// }
	//
	// if(crds.containsKey(v6)){
	// ind3=crds.get(v6);
	// }else{
	// ind3=num++;
	// crds.put(v6,ind3);
	// }
	//
	// if(crds.containsKey(v7)){
	// ind4=crds.get(v7);
	// }else{
	// ind4=num++;
	// crds.put(v7,ind4);
	// }
	//
	// ind = new ArrayList();
	// ind.add(ind4);
	// ind.add(ind2);
	// ind.add(ind1);
	// inds.add(ind);
	// ind = new ArrayList();
	// ind.add(ind4);
	// ind.add(ind1);
	// ind.add(ind3);
	// inds.add(ind);
	// }
	// //back face is visible
	// find = linvoxels.containsKey(v1-1);
	// if(!find)
	// {
	// if(crds.containsKey(v1)){
	// ind1=crds.get(v1);
	// }else{
	// ind1=num++;
	// crds.put(v1,ind1);
	// }
	//
	// if(crds.containsKey(v4)){
	// ind2=crds.get(v4);
	// }else{
	// ind2=num++;
	// crds.put(v4,ind2);
	// }
	//
	// if(crds.containsKey(v5)){
	// ind3=crds.get(v5);
	// }else{
	// ind3=num++;
	// crds.put(v5,ind3);
	// }
	//
	// if(crds.containsKey(v8)){
	// ind4=crds.get(v8);
	// }else{
	// ind4=num++;
	// crds.put(v8,ind4);
	// }
	//
	// ind = new ArrayList();
	// ind.add(ind4);
	// ind.add(ind2);
	// ind.add(ind1);
	// inds.add(ind);
	// ind = new ArrayList();
	// ind.add(ind4);
	// ind.add(ind1);
	// ind.add(ind3);
	// inds.add(ind);
	// }
	// //bottom face is visible
	// find = linvoxels.containsKey(v1-sx*sy);
	// if(!find)
	// {
	// if(crds.containsKey(v1)){
	// ind1=crds.get(v1);
	// }else{
	// ind1=num++;
	// crds.put(v1,ind1);
	// }
	//
	// if(crds.containsKey(v2)){
	// ind2=crds.get(v2);
	// }else{
	// ind2=num++;
	// crds.put(v2,ind2);
	// }
	//
	// if(crds.containsKey(v5)){
	// ind3=crds.get(v5);
	// }else{
	// ind3=num++;
	// crds.put(v5,ind3);
	// }
	//
	// if(crds.containsKey(v6)){
	// ind4=crds.get(v6);
	// }else{
	// ind4=num++;
	// crds.put(v6,ind4);
	// }
	//
	// ind = new ArrayList();
	// ind.add(ind3);
	// ind.add(ind2);
	// ind.add(ind1);
	// inds.add(ind);
	// ind = new ArrayList();
	// ind.add(ind3);
	// ind.add(ind4);
	// ind.add(ind2);
	// inds.add(ind);
	// }
	// //upper face is visible
	// find = linvoxels.containsKey(v1+sx*sy);
	// if(!find)
	// {
	// if(crds.containsKey(v3)){
	// ind1=crds.get(v3);
	// }else{
	// ind1=num++;
	// crds.put(v3,ind1);
	// }
	//
	// if(crds.containsKey(v4)){
	// ind2=crds.get(v4);
	// }else{
	// ind2=num++;
	// crds.put(v4,ind2);
	// }
	//
	// if(crds.containsKey(v7)){
	// ind3=crds.get(v7);
	// }else{
	// ind3=num++;
	// crds.put(v7,ind3);
	// }
	//
	// if(crds.containsKey(v8)){
	// ind4=crds.get(v8);
	// }else{
	// ind4=num++;
	// crds.put(v8,ind4);
	// }
	//
	// ind = new ArrayList();
	// ind.add(ind3);
	// ind.add(ind4);
	// ind.add(ind2);
	// inds.add(ind);
	// ind = new ArrayList();
	// ind.add(ind3);
	// ind.add(ind2);
	// ind.add(ind1);
	// inds.add(ind);
	// }
	// }
	//
	//
	// crds_srt = sortHashMapByValD(crds);
	//
	//
	// ret.add(crds_srt);
	// ret.add(inds);
	//
	// return ret;
	//
	// }
	// // End of
	// getCoordsAndIndicesVrml........................}}}</editor-fold>

	// ========================<editor-fold desc="getCoordsAndIndicesVrml">{{{
	private static ArrayList getCoordsAndIndicesVrml(ArrayList<Pnt3d_i> voxels,
			Pnt3d_i size) {
		//
		// intialiaze return values
		//
		ArrayList ret = new ArrayList();
		ArrayList<ArrayList<Integer>> inds = new ArrayList();
		LinkedHashMap<Integer, Integer> crds_srt = new LinkedHashMap();

		int sx = size.x;
		int sy = size.y;
		int sz = size.z;
		//
		// intialiaze
		//
		HashMap<Integer, Pnt3d_i> linvoxels = new HashMap();
		HashMap<Integer, Integer> crds = new HashMap();
		// TreeMap<Integer,Integer> crds = new TreeMap();
		ArrayList<Integer> ind = null;

		// these are vertices of a voxel
		Integer v1, v2, v3, v4, v5, v6, v7, v8;
		// indices per square face
		Integer ind1 = -1, ind2 = -1, ind3 = -1, ind4 = -1;

		// creat voxels container with linear indices
		for (int w = 0; w < voxels.size(); w++) {
			Pnt3d_i point = voxels.get(w);
			int linindex = indx2lindx(point, size);
			linvoxels.put(linindex, point);
		}

		int num = 0;
		for (int w = 0; w < voxels.size(); w++) {
			Pnt3d_i p1 = voxels.get(w);
			v1 = indx2lindx(p1, size);

			int i = p1.x;
			int j = p1.y;
			int k = p1.z;

			v2 = indx2lindx(i + 1, j, k, size);
			v3 = indx2lindx(i + 1, j, k + 1, size);
			v4 = indx2lindx(i, j, k + 1, size);
			v5 = indx2lindx(i, j + 1, k, size);
			v6 = indx2lindx(i + 1, j + 1, k, size);
			v7 = indx2lindx(i + 1, j + 1, k + 1, size);
			v8 = indx2lindx(i, j + 1, k + 1, size);

			// k
			//
			// |
			// |
			// |
			// |
			// |
			// |
			// ------------------------ j
			// /
			// /
			// /
			// /
			// /
			//
			// i
			//

			// the paths of the indices induce normals
			// with outside direction

			// left face is visible
			boolean find = linvoxels.containsKey(v1 - sx);
			if (!find) {

				if (crds.containsKey(v1)) {
					ind1 = crds.get(v1);
				} else {
					ind1 = num++;
					crds.put(v1, ind1);
				}

				if (crds.containsKey(v2)) {
					ind2 = crds.get(v2);
				} else {
					ind2 = num++;
					crds.put(v2, ind2);
				}

				if (crds.containsKey(v3)) {
					ind3 = crds.get(v3);
				} else {
					ind3 = num++;
					crds.put(v3, ind3);
				}

				if (crds.containsKey(v4)) {
					ind4 = crds.get(v4);
				} else {
					ind4 = num++;
					crds.put(v4, ind4);
				}

				ind = new ArrayList();
				ind.add(ind1);
				ind.add(ind2);
				ind.add(ind3);
				inds.add(ind);
				ind = new ArrayList();
				ind.add(ind1);
				ind.add(ind3);
				ind.add(ind4);
				inds.add(ind);
			}
			// right face is visible
			find = linvoxels.containsKey(v1 + sx);
			if (!find) {
				if (crds.containsKey(v5)) {
					ind1 = crds.get(v5);
				} else {
					ind1 = num++;
					crds.put(v5, ind1);
				}

				if (crds.containsKey(v6)) {
					ind2 = crds.get(v6);
				} else {
					ind2 = num++;
					crds.put(v6, ind2);
				}

				if (crds.containsKey(v7)) {
					ind3 = crds.get(v7);
				} else {
					ind3 = num++;
					crds.put(v7, ind3);
				}

				if (crds.containsKey(v8)) {
					ind4 = crds.get(v8);
				} else {
					ind4 = num++;
					crds.put(v8, ind4);
				}

				ind = new ArrayList();
				ind.add(ind1);
				ind.add(ind4);
				ind.add(ind3);
				inds.add(ind);
				ind = new ArrayList();
				ind.add(ind1);
				ind.add(ind3);
				ind.add(ind2);
				inds.add(ind);
			}
			// front face is visible
			find = linvoxels.containsKey(v1 + 1);
			if (!find) {
				if (crds.containsKey(v2)) {
					ind1 = crds.get(v2);
				} else {
					ind1 = num++;
					crds.put(v2, ind1);
				}

				if (crds.containsKey(v3)) {
					ind2 = crds.get(v3);
				} else {
					ind2 = num++;
					crds.put(v3, ind2);
				}

				if (crds.containsKey(v6)) {
					ind3 = crds.get(v6);
				} else {
					ind3 = num++;
					crds.put(v6, ind3);
				}

				if (crds.containsKey(v7)) {
					ind4 = crds.get(v7);
				} else {
					ind4 = num++;
					crds.put(v7, ind4);
				}

				ind = new ArrayList();
				ind.add(ind4);
				ind.add(ind2);
				ind.add(ind1);
				inds.add(ind);
				ind = new ArrayList();
				ind.add(ind4);
				ind.add(ind1);
				ind.add(ind3);
				inds.add(ind);
			}
			// back face is visible
			find = linvoxels.containsKey(v1 - 1);
			if (!find) {
				if (crds.containsKey(v1)) {
					ind1 = crds.get(v1);
				} else {
					ind1 = num++;
					crds.put(v1, ind1);
				}

				if (crds.containsKey(v4)) {
					ind2 = crds.get(v4);
				} else {
					ind2 = num++;
					crds.put(v4, ind2);
				}

				if (crds.containsKey(v5)) {
					ind3 = crds.get(v5);
				} else {
					ind3 = num++;
					crds.put(v5, ind3);
				}

				if (crds.containsKey(v8)) {
					ind4 = crds.get(v8);
				} else {
					ind4 = num++;
					crds.put(v8, ind4);
				}

				ind = new ArrayList();
				ind.add(ind4);
				ind.add(ind2);
				ind.add(ind1);
				inds.add(ind);
				ind = new ArrayList();
				ind.add(ind4);
				ind.add(ind1);
				ind.add(ind3);
				inds.add(ind);
			}
			// bottom face is visible
			find = linvoxels.containsKey(v1 - sx * sy);
			if (!find) {
				if (crds.containsKey(v1)) {
					ind1 = crds.get(v1);
				} else {
					ind1 = num++;
					crds.put(v1, ind1);
				}

				if (crds.containsKey(v2)) {
					ind2 = crds.get(v2);
				} else {
					ind2 = num++;
					crds.put(v2, ind2);
				}

				if (crds.containsKey(v5)) {
					ind3 = crds.get(v5);
				} else {
					ind3 = num++;
					crds.put(v5, ind3);
				}

				if (crds.containsKey(v6)) {
					ind4 = crds.get(v6);
				} else {
					ind4 = num++;
					crds.put(v6, ind4);
				}

				ind = new ArrayList();
				ind.add(ind3);
				ind.add(ind2);
				ind.add(ind1);
				inds.add(ind);
				ind = new ArrayList();
				ind.add(ind3);
				ind.add(ind4);
				ind.add(ind2);
				inds.add(ind);
			}
			// upper face is visible
			find = linvoxels.containsKey(v1 + sx * sy);
			if (!find) {
				if (crds.containsKey(v3)) {
					ind1 = crds.get(v3);
				} else {
					ind1 = num++;
					crds.put(v3, ind1);
				}

				if (crds.containsKey(v4)) {
					ind2 = crds.get(v4);
				} else {
					ind2 = num++;
					crds.put(v4, ind2);
				}

				if (crds.containsKey(v7)) {
					ind3 = crds.get(v7);
				} else {
					ind3 = num++;
					crds.put(v7, ind3);
				}

				if (crds.containsKey(v8)) {
					ind4 = crds.get(v8);
				} else {
					ind4 = num++;
					crds.put(v8, ind4);
				}

				ind = new ArrayList();
				ind.add(ind3);
				ind.add(ind4);
				ind.add(ind2);
				inds.add(ind);
				ind = new ArrayList();
				ind.add(ind3);
				ind.add(ind2);
				ind.add(ind1);
				inds.add(ind);
			}
		}

		// bottle neck - very bad sorting
		// crds_srt = sortHashMapByValD(crds);

		// perfect
		Map<Integer, Integer> crds2 = crds;
		crds2 = MapUtil.sortByValue(crds2);

		// ret.add(crds_srt);
		ret.add(crds2);
		ret.add(inds);

		return ret;

	}

	// End of getCoordsAndIndicesVrml........................}}}</editor-fold>

	// ============================<editor-fold desc="makeShapeVrml">{{{
	private static void makeShapeVrml(Pnt3d_i size, StringBuilder vrml,
			double red, double green, double blue,
			LinkedHashMap<Integer, Integer> crds_srt,
			ArrayList<ArrayList<Integer>> inds) {

		// some VRML attributes:
		// transparency
		double transparency = 0.0;

		String line = "";
		int last = -1;

		// begin add Shape
		vrml.append("Shape {\n");

		// begin add Appearance
		vrml.append("    appearance Appearance {\n");
		vrml.append("        material Material {\n");
		vrml.append("        diffuseColor " + String.valueOf(red) + " "
				+ String.valueOf(green) + " " + String.valueOf(blue) + "\n");
		vrml.append("        transparency " + String.valueOf(transparency)
				+ "\n");
		;
		vrml.append("        }\n");
		vrml.append("    }\n");

		// begin add geometry IndexedFaceSet
		vrml.append("    geometry IndexedFaceSet {\n");
		// begin add coord Coordinate
		vrml.append("        coord Coordinate {\n");
		// begin add point section - points (coordinates)
		vrml.append("point [\n");
		// begin add point section data - points (coordinates)
		Object[] keys = crds_srt.keySet().toArray();
		Object[] vals = crds_srt.values().toArray();
		last = (int) crds_srt.size() - 1;
		int last_key = (Integer) keys[last];
		Pnt3d_i last_vox = lindx2indx(last_key, size);
		for (int i = 0; i < last; i++) {

			Integer key = (Integer) keys[i];
			Pnt3d_i vox = lindx2indx(key, size);

			line = String.valueOf(vox.x) + " " + String.valueOf(vox.y) + " "
					+ String.valueOf(vox.z) + " " + "," + "\n";
			vrml.append(line);
		}
		// no comma after x, y, z
		line = String.valueOf(last_vox.x) + " " + String.valueOf(last_vox.y)
				+ " " + String.valueOf(last_vox.z) + " " + "\n";
		vrml.append(line);
		// end add point section data - points (coordinates)

		// end add point section data - points (coordinates)
		vrml.append("]\n");

		// end add coord Coordinate
		vrml.append(" }\n");

		// begin add coordIndex section - coordinates indices
		vrml.append("coordIndex [\n");
		// add coordIndex section data - coordinates indices
		last = inds.size() - 1;
		ArrayList<Integer> last_indx_set = inds.get(last);
		for (int i = 0; i < last; i++) {

			ArrayList<Integer> indx_set = inds.get(i);

			line = String.valueOf(indx_set.get(0)) + ","
					+ String.valueOf(indx_set.get(1)) + ","
					+ String.valueOf(indx_set.get(2)) + "," + "-1" + ",";
			vrml.append(line);
		}
		// no comma after -1
		line = String.valueOf(last_indx_set.get(0)) + ","
				+ String.valueOf(last_indx_set.get(1)) + ","
				+ String.valueOf(last_indx_set.get(2)) + "," + "-1";
		vrml.append(line);
		// end add coordIndex section data - coordinates indices

		// end add coordIndex section - coordinates indices
		vrml.append("]\n");

		// end add geometry IndexedFaceSet
		vrml.append(" }\n");

		// end add Shape
		vrml.append("}\n");
	}

	// End of makeShapeVrml...........................}}}</editor-fold>

	// ============================<editor-fold desc="indx2lindx">{{{
	private static int indx2lindx(int i, int j, int k, Pnt3d_i size) {

		int sx = size.x;
		int sy = size.y;
		int sz = size.z;

		return k * sy * sx + j * sx + i;
	};

	// End of indx2lindx...........................}}}</editor-fold>

	// ============================<editor-fold desc="indx2lindx">{{{
	private static int indx2lindx(Pnt3d_i point, Pnt3d_i size) {

		int sx = size.x;
		int sy = size.y;
		int sz = size.z;

		int x = point.x;
		int y = point.y;
		int z = point.z;

		return z * sy * sx + y * sx + x;
	};

	// End of indx2lindx...........................}}}</editor-fold>

	// ============================<editor-fold desc="lindx2indx">{{{
	private static Pnt3d_i lindx2indx(int ind, Pnt3d_i size) {

		int sx = size.x;
		int sy = size.y;
		int sz = size.z;

		int z = (int) Math.floor((double) ind / (double) (sy * sx));
		int y = (int) Math.floor((double) (ind - z * sy * sx) / (double) sx);
		int x = ind - z * sy * sx - y * sx;

		return new Pnt3d_i(x, y, z);

	};

	// End of lindx2indx...........................}}}</editor-fold>

	// ============================<editor-fold desc="sortHashMapByValD">{{{
	private static LinkedHashMap<Integer, Integer> sortHashMapByValD(
			HashMap passedMap) {
		List mapKeys = new ArrayList(passedMap.keySet());
		List mapValues = new ArrayList(passedMap.values());
		Collections.sort(mapValues);
		Collections.sort(mapKeys);

		LinkedHashMap SrtMap = new LinkedHashMap();

		Iterator valueIt = mapValues.iterator();
		while (valueIt.hasNext()) {
			Object val = valueIt.next();
			Iterator keyIt = mapKeys.iterator();

			while (keyIt.hasNext()) {
				Object key = keyIt.next();
				String comp1 = passedMap.get(key).toString();
				String comp2 = val.toString();

				if (comp1.equals(comp2)) {
					passedMap.remove(key);
					mapKeys.remove(key);
					SrtMap.put((Integer) key, (Integer) val);
					break;
				}
			}
		}
		return SrtMap;
	}

	// End of sortHashMapByValD...........................}}}</editor-fold>

	// ============================<editor-fold desc="getComponentNumber">{{{
	private static int getComponentNumber(File f, ComponentsFileFilter cff) {

		int num = -1;

		String fn = f.getName();

		String filter = cff.getFilter();
		int ind = fn.indexOf(filter);
		String underscore = filter.substring(filter.length() - 1,
				filter.length());
		int ind2 = fn.indexOf(underscore, ind);
		String s_num = fn.substring(ind2 + 1);

		try {
			num = Integer.parseInt(s_num);
		} catch (NumberFormatException exp) {
			String ErrMsg = "file name is not valid:" + f.getName();
			throw new VrmlUtilException(ErrMsg, exp);
		}

		return num;
	};

	// End of getComponentNumber...........................}}}</editor-fold>

	// ============================<editor-fold desc="getVoxels">{{{
	/*
	 * get voxels from file
	 */
	private static ArrayList getVoxels(File file) {

		// initialize return values
		ArrayList ret = new ArrayList();

		ArrayList voxels = processContents(file);

		ArrayList<Integer> xs = (ArrayList<Integer>) voxels.get(0);
		ArrayList<Integer> ys = (ArrayList<Integer>) voxels.get(1);
		ArrayList<Integer> zs = (ArrayList<Integer>) voxels.get(2);
		ArrayList<Pnt3d_i> points = (ArrayList<Pnt3d_i>) voxels.get(3);

		int xmin = Collections.min(xs);
		int xmax = Collections.max(xs);

		int ymin = Collections.min(ys);
		int ymax = Collections.max(ys);

		int zmin = Collections.min(zs);
		int zmax = Collections.max(zs);

		System.out.println("xmin=" + xmin + " --- " + "xmax=" + xmax);
		System.out.println("ymin=" + ymin + " --- " + "ymax=" + ymax);
		System.out.println("zmin=" + zmin + " --- " + "zmax=" + zmax);

		int offset = 1;
		int sx = xmax + 1 + 2 * offset;
		int sy = ymax + 1 + 2 * offset;
		int sz = zmax + 1 + 2 * offset;
		System.out.println("file=" + file.getAbsolutePath());
		System.out.println("offset=" + offset);
		System.out.println("box size with offset:");
		System.out.println("sx=" + sx);
		System.out.println("sy=" + sy);
		System.out.println("sz=" + sz);

		Pnt3d_i size = new Pnt3d_i(sx, sy, sz);

		ret.add(points);
		ret.add(size);

		return ret;

	}

	// End of getVoxels...........................}}}</editor-fold>

	// ============================<editor-fold desc="processContents">{{{
	static private ArrayList processContents(File file) {

		ArrayList voxels = new ArrayList();

		ArrayList<Integer> xs = new ArrayList();
		ArrayList<Integer> ys = new ArrayList();
		ArrayList<Integer> zs = new ArrayList();
		ArrayList<Pnt3d_i> pnts = new ArrayList();

		long count = 1;
		try {
			BufferedReader inf = new BufferedReader(new FileReader(file));

			try {
				String line = null; // not declared within while loop
				/*
				 * it returns the content of a line MINUS the newline. it
				 * returns null only for the END of the stream. it returns an
				 * empty String if two newlines appear in a row.
				 */
				while ((line = inf.readLine()) != null) {

					// process line
					// System.out.print(line);
					// System.out.print("\n");

					// skip the first two lines in the voxels file:
					// -- Extra Info line
					// -- Number of the voxels
					if (count > 2) {
						Pnt3d_i pnt = processLine(line, count, file);
						xs.add(pnt.x);
						ys.add(pnt.y);
						zs.add(pnt.z);
						pnts.add(pnt);
					}

					count++;
				}
			} catch (IOException ex) {
				System.out.println(ex.toString());
			} finally {
				inf.close();
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}

		voxels.add(xs);
		voxels.add(ys);
		voxels.add(zs);
		voxels.add(pnts);

		return voxels;

	}

	// End of processContents...........................}}}</editor-fold>

	// ============================<editor-fold desc="processLine">{{{
	static private Pnt3d_i processLine(String line, long count, File file)
			throws Exception {

		String SEPARATOR = " ";

		// get data
		String tokens[] = null;
		String result = "";
		int x = -1;
		int y = -1;
		int z = -1;

		try {
			// get the first part only
			tokens = line.split(SEPARATOR);
			// System.out.print(tokens.length);
			int len = tokens.length;
			// just check this
			if (tokens == null) {
				// skip the line
				return null;
			}
			if (len == 3) {
				// 1. x
				x = Integer.parseInt(tokens[0]);
				// 2. y
				y = Integer.parseInt(tokens[1]);
				// 3. z
				z = Integer.parseInt(tokens[2]);

			} else {
				// bad line - never happens, but ...
			}
		} catch (Exception exp) {
			// String.valueOf(countChr) +" ||| "+
			result = x + " ||| " + y + " ||| " + z;
			;
			System.out.println("While processing file: " + file);
			System.out.println("Error: " + exp.toString());
			System.out.println("Size of the buffer array tokens.length="
					+ tokens.length);
			System.out.println("Not a valid row number=" + count + ":");
			System.out.println(result);
			// interrupt processing
			throw new Exception(exp);
		}

		return new Pnt3d_i(x, y, z);
	}

	// End of processLine...........................}}}</editor-fold>

	// ===================================<editor-fold desc="saveToFile">{{{
	/**
	 * Save string data to file.
	 * 
	 * @param file
	 *            file
	 * @param contents
	 *            data
	 * @throws IOException
	 */
	@SuppressWarnings("static-access")
	private static void saveToFile(File file, String contents)
			throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("File should not be null.");
		}

		if (!file.exists()) {
			file.createNewFile();
		}
		// use buffering
		// Writer output = new BufferedWriter(new FileWriter(file));
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "UTF8"));

		try {
			output.write(contents);
			// //test error
			// int a = 5;
			// int b = 0;
			// int c = a/b;
		} catch (UnsupportedEncodingException ue) {

			System.out.println("Not supported : ");
			throw new UnsupportedEncodingException("File cannot be written. "
					+ "UTF-8 not supported: " + file + "\nError:"
					+ ue.getMessage());
		} finally {
			output.close();
		}
	}

	// End of saveToFile...........................}}}</editor-fold>

	// ============================<editor-fold desc="ComponentsFileFilter">{{{
	public static class ComponentsFileFilter implements FileFilter {
		private final String filter = ".out_";

		public ComponentsFileFilter() {
			// this.filter = filter;
		}

		public String getFilter() {
			return filter;
		}

		@Override
		public boolean accept(File file) {
			boolean exists_file = file.exists() && !file.isDirectory();

			String fn = file.getName();
			int ind = fn.indexOf(filter);
			int ind2 = -1;
			if (ind > -1)
				ind2 = fn.indexOf(".", ind + 1);
			if (exists_file && ind > -1 && ind2 == -1) {
				return true;
			}

			return false;
		}
	}

	// End of ComponentsFileFilter...........................}}}</editor-fold>

	// ============================<editor-fold desc="MainVoxelsFileFilter">{{{
	public static class MainVoxelsFileFilter implements FileFilter {
		private final String filter = ".out";

		public MainVoxelsFileFilter() {
			// this.filter = filter;
		}

		public String getFilter() {
			return filter;
		}

		@Override
		public boolean accept(File file) {
			boolean exists_file = file.exists() && !file.isDirectory();

			String fn = file.getName();
			if (exists_file && fn.endsWith(filter)) {
				return true;
			}

			return false;
		}
	}

	// End of MainVoxelsFileFilter...........................}}}</editor-fold>

	// ============================<editor-fold desc="VrmlUtilException">{{{
	public static class VrmlUtilException extends RuntimeException {
		public VrmlUtilException(String msg) {
			super(msg);
		}

		public VrmlUtilException(String msg, Throwable th) {
			super(msg, th);
		}
	}
	// End of launchJavaJar...........................}}}</editor-fold>

}