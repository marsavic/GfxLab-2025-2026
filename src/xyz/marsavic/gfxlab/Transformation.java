package xyz.marsavic.gfxlab;


import xyz.marsavic.functions.F1;


public interface Transformation extends F1<Vec3, Vec3> {
	
	default Transformation then(Transformation outer) {
		return p -> outer.at(at(p));
	}
	
	
	// --------------------
	
	
	Transformation identity = p -> p;


	double EPS = 0x1p-16;
	Vec3 EPS_X = Vec3.EX.mul(EPS);
	Vec3 EPS_Y = Vec3.EY.mul(EPS);
	Vec3 EPS_Z = Vec3.EZ.mul(EPS);
	
	default Affine gradient(Vec3 p) {
		Vec3 a = at(p);
		return Affine.unitVectors(
				(at(p.add(EPS_X)).sub(a)).div(EPS),
				(at(p.add(EPS_Y)).sub(a)).div(EPS),
				(at(p.add(EPS_Z)).sub(a)).div(EPS)
		);
	}

	default Affine gradient(Vec3 p, double eps) {
		Vec3 a = at(p);
		return Affine.unitVectors(
				(at(p.add(Vec3.EX.mul(eps))).sub(a)).div(eps),
				(at(p.add(Vec3.EY.mul(eps))).sub(a)).div(eps),
				(at(p.add(Vec3.EZ.mul(eps))).sub(a)).div(eps)
		);
	}

	
	static Transformation chain(Transformation... transformations) {
		Transformation res = Transformation.identity;
		for (Transformation t : transformations) {
			res = res.then(t);
		}
		return res;
	}
	
	static Transformation chain(Affine... transformations) {
		Transformation res = Affine.IDENTITY;
		for (Transformation t : transformations) {
			res = res.then(t);
		}
		return res;
	}
}
