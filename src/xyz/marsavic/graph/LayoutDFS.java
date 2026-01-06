package xyz.marsavic.graph;

import xyz.marsavic.geometry.Box;
import xyz.marsavic.geometry.Vector;
import xyz.marsavic.reactions.elements.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class LayoutDFS {
	private static final Vector margin = Vector.xy(30, 10);
	
	private static class Data {
		Box box;
		Vector sizeSubtree, sizeChildrenSubtrees;
	}
	
	
	private final Map<Element, List<Edge>> map_inputEdges;
	private final Map<Element, Data> map_data = new HashMap<>();
	
	public final Map<Element, Box> result;
	
	
	public LayoutDFS(Map<Element, Box> boxes, Map<Element, List<Edge>> map_inputEdges) {
		this.map_inputEdges = map_inputEdges;
		
		for (Map.Entry<Element, Box> entry : boxes.entrySet()) {
			Element e = entry.getKey();
			Data data = new Data();
			data.box = entry.getValue().extend(margin);
			map_data.put(e, data);
		}
		
		for (Element e : map_data.keySet()) {
			if (e.outputs().isEmpty()) {
				dfs0(e);
				dfs1(e);
			}
		}
		
		result = map_data.entrySet().stream().collect(Collectors.toMap(
				Map.Entry::getKey,
				e -> e.getValue().box.extend(margin.inverse())
		));
	}
	
	
	private void dfs0(Element e) {
		double w = 0;
		double h = 0;
		
		for (Edge edge : map_inputEdges.get(e)) {
			Element eChild = edge.output().element();
			dfs0(eChild);
			Data dataChild = map_data.get(eChild);
			Vector dSubtree = dataChild.sizeSubtree;
			w = Math.max(w, dSubtree.x());
			h += dSubtree.y();
		}
		
		Data dataMe = map_data.get(e);
		dataMe.sizeChildrenSubtrees = Vector.xy(w, h);
		w += dataMe.box.d().x();
		h = Math.max(h, dataMe.box.d().y());
		dataMe.sizeSubtree = Vector.xy(w, h).add(margin);
	}
	
	
	private void dfs1(Element e) {
		Data dataV = map_data.get(e);
		
		double x = dataV.box.x().p();
		double y = dataV.box.y().c() - dataV.sizeChildrenSubtrees.y() / 2;
		
		for (Edge edge : map_inputEdges.get(e)) {
			Element eChild = edge.output().element();
			Data dataChild = map_data.get(eChild);
			
			double ys = y + (dataChild.sizeSubtree.y() - dataChild.box.y().d()) / 2;
			
			dataChild.box = Box.pd(
					Vector.xy(x - dataChild.box.d().x(), ys),
					dataChild.box.d()
			);
			y += dataChild.sizeSubtree.y();
			dfs1(eChild);
		}
	}
	
}
