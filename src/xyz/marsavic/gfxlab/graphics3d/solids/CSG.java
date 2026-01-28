package xyz.marsavic.gfxlab.graphics3d.solids;

import xyz.marsavic.gfxlab.graphics3d.Hit;
import xyz.marsavic.gfxlab.graphics3d.Ray;
import xyz.marsavic.gfxlab.graphics3d.Solid;

import java.util.Objects;

public final class CSG implements Solid {
	private final int k;
	private final Solid[] solids;
	
	
	private CSG(int k, Solid... solids) {
		this.k = k;
		this.solids = solids;
	}
	
	public static Solid complement(Solid solid) {
		return (ray, afterTime) -> solid.firstHit(ray, afterTime).inverted();
	}
	
	/**
	 * The solid made of all the points contained in at least k of the given solids.
	 */
	public static CSG atLeast(int k, Solid... solids) {
		return new CSG(k, solids);
	}
	
	public static CSG union(Solid... solids) {
		return atLeast(1, solids);
	}
	
	public static CSG intersection(Solid... solids) {
		return atLeast(solids.length, solids);
	}
	
	public static CSG difference(Solid solidA, Solid solidB) {
		return intersection(solidA, complement(solidB));
	}
	
	
	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		int n = solids.length;
		
		Hit[] hits = new Hit[n];
		int[] d = new int[n];
		int inCount = n;
		
		for (int i = 0; i < n; i++) {
			Hit hit = solids[i].firstHit(ray, afterTime);
			hits[i] = hit;
			boolean in = ray.d().dot(hit.n()) > 0;
			d[i] = in ? -1 : 1;
			inCount -= d[i];
		}
		inCount /= 2;
		
		boolean inResultingSolid = inCount >= k;
		int target = inResultingSolid ? k - 1 : k;
		
		while (true) {
			double tFirst = Double.POSITIVE_INFINITY;
			int iFirst = -1;
			for (int i = 0; i < n; i++) {
				double t = hits[i].t();
				if (t < tFirst) {
					tFirst = t;
					iFirst = i;
				}
			}
			
			if (tFirst == Double.POSITIVE_INFINITY) {
				return Hit.AtInfinity.axisAligned(ray.d(), inResultingSolid);
			}
			Hit hitFirst = hits[iFirst];
			
			inCount += d[iFirst];
			d[iFirst] = -d[iFirst];
			if (inCount == target) {
				return hitFirst;
			}
			
			hits[iFirst] = solids[iFirst].firstHit(ray, tFirst);
		}
	}

}
