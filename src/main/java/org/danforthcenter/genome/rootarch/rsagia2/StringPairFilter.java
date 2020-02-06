/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.danforthcenter.genome.rootarch.rsagia2;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author bm93
 */
public class StringPairFilter {
	public static String EXACT_REGEX = "([a-zA-Z0-9]+)";
	public static String ONWARD_REGEX = "(" + EXACT_REGEX + "-" + ")";
	public static String UPTO_REGEX = "(" + "-" + EXACT_REGEX + ")";
	public static String RANGE_REGEX = "(" + EXACT_REGEX + "-" + EXACT_REGEX
			+ ")";
	public static String LIST_REGEX = "^(" + EXACT_REGEX + "|" + ONWARD_REGEX
			+ "|" + UPTO_REGEX + "|" + RANGE_REGEX + ")(,(" + EXACT_REGEX + "|"
			+ ONWARD_REGEX + "|" + UPTO_REGEX + "|" + RANGE_REGEX + "))*$";
	public static String LIST_REGEX_PLANT_DAY = "^(" + EXACT_REGEX + ")(,("
			+ EXACT_REGEX + "))*$";

	protected String r1;
	protected String r2;
	protected String r3;

	public String getR1()
	{
		return r1;
	}

	public String getR2()
	{
		return r2;
	}

	public String getR3()
	{
		return r3;
	}

	public static boolean isValid(String s) {
		Pattern p = Pattern.compile(LIST_REGEX);
		Matcher m = p.matcher(s);
		return m.matches();
	}

	public static boolean isValidPlantDay(String s) {
		Pattern p = Pattern.compile(LIST_REGEX_PLANT_DAY);
		Matcher m = p.matcher(s);
		return m.matches();
	}

	public static ArrayList<StringPairFilter> getInstances(String s) {
		ArrayList<StringPairFilter> ans = new ArrayList<StringPairFilter>();
		if (s.length() > 0) {
			String[] ss = s.split(",");
			for (int i = 0; i < ss.length; i++) {
				ans.add(StringPairFilter.getInstance(ss[i]));
			}
		}

		return ans;
	}

	protected static StringPairFilter getInstance(String s) {
		StringPairFilter ans = null;

		Pattern p = Pattern.compile(RANGE_REGEX);
		Matcher m = p.matcher(s);
		if (m.matches()) {
			ans = new StringPairFilter(m.group(2), m.group(3), null);
		} else {
			p = Pattern.compile(UPTO_REGEX);
			m = p.matcher(s);
			if (m.matches()) {
				ans = new StringPairFilter(null, m.group(2), null);
			} else {
				p = Pattern.compile(ONWARD_REGEX);
				m = p.matcher(s);
				if (m.matches()) {
					ans = new StringPairFilter(m.group(2), null, null);
				} else {
					p = Pattern.compile(EXACT_REGEX);
					m = p.matcher(s);
					if (m.matches()) {
						ans = new StringPairFilter(null, null, m.group(1));
					}
				}
			}
		}

		return ans;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final StringPairFilter other = (StringPairFilter) obj;
		if ((this.r1 == null) ? (other.r1 != null) : !this.r1.equals(other.r1)) {
			return false;
		}
		if ((this.r2 == null) ? (other.r2 != null) : !this.r2.equals(other.r2)) {
			return false;
		}
		if ((this.r3 == null) ? (other.r3 != null) : !this.r3.equals(other.r3)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 37 * hash + (this.r1 != null ? this.r1.hashCode() : 0);
		hash = 37 * hash + (this.r2 != null ? this.r2.hashCode() : 0);
		hash = 37 * hash + (this.r3 != null ? this.r3.hashCode() : 0);
		return hash;
	}

	public StringPairFilter(String r1, String r2, String r3) {
		this.r1 = r1;
		this.r2 = r2;
		this.r3 = r3;
	}

	public boolean accept(String s) {
		boolean ans = false;
		if (r3 != null) {
			ans = r3.equals(s);
		} else if (r1 == null && r2 != null) {
			ans = s.compareTo(r2) < 1;
		} else if (r1 != null && r2 == null) {
			ans = s.compareTo(r1) > -1;
		} else {
			return s.compareTo(r1) > -1 && s.compareTo(r2) < 1;
		}

		return ans;
	}

	public static String toString(ArrayList<StringPairFilter> list) {
		String ans = "";
		if (list.size() > 0) {
			ans = list.get(0).toString();
		}
		for (int i = 1; i < list.size(); i++) {
			ans += "," + list.get(i);
		}

		return ans;
	}

	@Override
	public String toString() {
		String ans = null;
		if (r3 != null) {
			return r3;
		} else {
			ans = ((r1 == null) ? "" : r1) + "-" + ((r2 == null) ? "" : r2);
		}

		return ans;
	}
}
