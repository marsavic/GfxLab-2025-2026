package xyz.marsavic.gfxlab.gui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import xyz.marsavic.functions.A0;
import xyz.marsavic.functions.A1;
import xyz.marsavic.gfxlab.ElementAnimationSink;
import xyz.marsavic.graph.Graph;
import xyz.marsavic.graph.Vertex;
import xyz.marsavic.graph.VertexInputJack;
import xyz.marsavic.gfxlab.playground.GfxLab;
import xyz.marsavic.gfxlab.resources.Resources;
import xyz.marsavic.graph.Vertex_AnimationSink;
import xyz.marsavic.javafx.UtilsFX;
import xyz.marsavic.reactions.elements.Element;


public class App extends Application {

	static {
		System.setProperty("prism.forceGPU=true", "true");
//		System.setProperty("javafx.animation.fullspeed", "true");
//		System.setProperty("javafx.animation.pulse", "60");
//		System.setProperty("prism.vsync", "true");
//		System.setProperty("quantum.multithreaded", "false");
	}
	
	Stage primaryStage;
	Pane root;
	Graph graph;
	Region info;
	TextArea textArea;
	
	
	
	AnimationTimer animationTimer = new AnimationTimer() {
		int i = 0;
		@Override
		public void handle(long now) {
			textArea.setText(Profiling.infoTextSystem() + Profiling.infoTextProfilers());
			i++;
			if ((i % 20) == 0) {				
				primaryStage.getScene().getStylesheets().setAll("file:resources/xyz/marsavic/gfxlab/resources/mars-dark2.css");
			}
		}
	};
	
	
	
	void addElements(Graph graph, Element e) {
		Vertex vertex = graph.createVertex(e);
		
		for (VertexInputJack inputJack : vertex.inputJacks()) {
			Element.Input<?> input = inputJack.input;
			Element.Output<?> output = input.output();
			Element eChild = output.element();
			addElements(graph, eChild);
			graph.createConnection(output, input);
		}
	}
	
	
	void initGraph(Graph graph) {
		var sink = new ElementAnimationSink(GfxLab.setup());		
		
		addElements(graph, sink);
		
		// Adding onResized for animation sinks 
		for (Vertex v : graph.vertices) {
			if (v instanceof Vertex_AnimationSink vas) {
				vas.onResized().add(onResized);
			}
		}
	}
	
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		
		primaryStage.setTitle("GFX Lab");
		
		graph = new Graph();
		initGraph(graph);
		
		textArea = new TextArea();
		textArea.setEditable(false);
		info = textArea;
		
		root = new Pane(
				graph
		);
		
		graph   .prefWidthProperty ().bind(root.widthProperty ());
		graph   .prefHeightProperty().bind(root.heightProperty());
//		textArea.prefWidthProperty ().bind(root.widthProperty ());
//		textArea.prefHeightProperty().bind(root.heightProperty());
		textArea.setPrefWidth(800);
		textArea.setPrefHeight(400);
		
		textArea.setMouseTransparent(true);
		
		Scene scene = new Scene(root, 1840, 1000);
		
		
		scene.getStylesheets().setAll(Resources.stylesheetURL);
		primaryStage.getIcons().setAll(Resources.iconsApplication());

		primaryStage.setFullScreenExitHint("");
		
		primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			A0 action = switch (event.getCode()) {
				case ESCAPE -> Platform::exit;
				case F1  -> this::autoPosition;
				case F2  -> () -> graph.jiggle();
				case F3  -> () -> graph.layItOutFX();
				case F4  -> this::toggleInfo;
				case F11 -> () -> primaryStage.setFullScreen(!primaryStage.isFullScreen());
				default -> A0.NOOP;
			};
			
			action.at();
		});

		primaryStage.setScene(scene);
		primaryStage.show();
		
//		Platform.runLater(this::autoPosition);
		
		
//		System.err.println("Using animation timer for CSS auto reload. Remember to remove");
		animationTimer.start();		
	}
	
	
	private void toggleInfo() {
		UtilsFX.toggle(root, info);
	}
	
	
	private void autoPosition() {
		graph.layItOutFX();
		graph.jiggle();
	}
	
	
	private final A1<Vertex_AnimationSink.EventResized> onResized = this::onResized;
	
	private void onResized(Vertex_AnimationSink.EventResized eventResized) {
		Platform.runLater(this::autoPosition);
	}
	
	
	static void main() {
		launch();
	}
	
	
}
