package view;

import com.brunomnsilva.smartgraph.containers.SmartGraphDemoContainer;
import com.brunomnsilva.smartgraph.graph.*;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import model.Airport;
import model.Flight;

import java.lang.reflect.AccessibleObject;
import java.util.Collection;

public class FlightsView extends BorderPane {

    /** The graph model */
    private final Graph<Airport, Flight> graph;

    /** Graph visualization panel */
    private final SmartGraphPanel<Airport, Flight> graphView;

    /** displays total number of airports */
    private Label labelNumberAirports;

    /** displays total number of flights */
    private Label labelNumberFlights;

    /** displays the airport with the most inbound/outbound flights */
    private Label labelBusiestAirport;

    /** displays the number of inbound/outbound flights of the busiest airport */
    private Label labelBusiestAirportNumberFlights;

    /** */

    private ObservableList<Vertex<Airport>> listAirportsToRemove;
    private ObservableList<Vertex<Airport>> listAirportsFrom;
    private ObservableList<Vertex<Airport>> listAirportsTo;
    private ObservableList<Edge<Flight, Airport>> listFlights;

    public FlightsView() {
        this.graph = new GraphEdgeList<>();
        createInitialModel();

        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
        this.graphView = new SmartGraphPanel<>(graph, strategy);

        doLayout();
    }

    private void createInitialModel() {
        // TODO: create flight model - Figure 1 of assignment
        Vertex<Airport> sfo = graph.insertVertex(new Airport("SFO"));
        Vertex<Airport> ord = graph.insertVertex(new Airport("ORD"));
        Vertex<Airport> pvd = graph.insertVertex(new Airport("PVD"));
        Vertex<Airport> mia = graph.insertVertex(new Airport("MIA"));
        Vertex<Airport> lga = graph.insertVertex(new Airport("LGA"));
        Vertex<Airport> dfw = graph.insertVertex(new Airport("DFW"));
        Vertex<Airport> lax = graph.insertVertex(new Airport("LAX"));
        Vertex<Airport> hnl = graph.insertVertex(new Airport("HNL"));

        Edge<Flight, Airport> f1 = graph.insertEdge(hnl, lax, new Flight("UN3563", 2555));
        Edge<Flight, Airport> f2 = graph.insertEdge(lax, hnl, new Flight("DT1597", 2555));

        Edge<Flight, Airport> f3 = graph.insertEdge(lax, sfo, new Flight("UN9375", 337));
        Edge<Flight, Airport> f4 = graph.insertEdge(sfo, lax, new Flight("AM4526", 337));

        Edge<Flight, Airport> f5 = graph.insertEdge(lax, ord, new Flight("UN4836", 1743));
        Edge<Flight, Airport> f6 = graph.insertEdge(ord, lax, new Flight("VA2001", 1743));

        Edge<Flight, Airport> f7 = graph.insertEdge(sfo, ord, new Flight("UN1475", 1843));
        Edge<Flight, Airport> f8 = graph.insertEdge(ord, sfo, new Flight("AL7854", 1843));

        Edge<Flight, Airport> f9 = graph.insertEdge(lax, dfw, new Flight("SP1020", 1233));
        Edge<Flight, Airport> f10 = graph.insertEdge(dfw, lax, new Flight("AM4582", 1233));

        Edge<Flight, Airport> f11 = graph.insertEdge(ord, dfw, new Flight("UN4568", 802));

        Edge<Flight, Airport> f12 = graph.insertEdge(dfw, lga, new Flight("SP4512", 1387));

        Edge<Flight, Airport> f13 = graph.insertEdge(ord, pvd, new Flight("AM4520", 849));
        Edge<Flight, Airport> f14 = graph.insertEdge(pvd, ord, new Flight("UN7812", 849));

        Edge<Flight, Airport> f15 = graph.insertEdge(pvd, mia, new Flight("FT1000", 1205));

        Edge<Flight, Airport> f16 = graph.insertEdge(mia, lga, new Flight("FT4021", 1099));
        Edge<Flight, Airport> f17 = graph.insertEdge(lga, mia, new Flight("AM1026", 1099));

        Edge<Flight, Airport> f18 = graph.insertEdge(dfw, mia, new Flight("AM5267", 1120));



    }

    private void addAirport(String airportCode) {
        // TODO: implement, check for errors and use showError(message) in case of error
        //  code must be valid, i.e., not empty
        if(airportCode.isBlank() || airportCode.isEmpty()){
            showError("The airport code cannot be blank or empty!");
            return;
        }
        graph.insertVertex(new Airport(airportCode));
    }

    private void addFlight(Vertex<Airport> vertexFrom, Vertex<Airport> vertexTo, String code, String distance) {
        // TODO: implement, check for errors and use showError(message) in case of error
        //  Cannot add flights with the same airport as inbound/outbound
        if(vertexFrom == null || vertexTo == null){
            showError("One of the airports is null.");
            return;
        }

        if(vertexFrom.equals(vertexTo)){
            showError("The destination cannot be the same as the departure!");
            return;
        }

        if(code.isBlank()){
            showError("Code field is empty.");
            return;
        }

        if(distance.isBlank()){
            showError("Distance field is empty.");
            return;
        }

        double d = Double.parseDouble(distance);
        if(d == 0){
            showError("The distance between two airports cannot be zero!");
            return;
        }

        graph.insertEdge(vertexFrom, vertexTo, new Flight(code, d));
    }

    private void removeFlight(Edge<Flight, Airport> edge) {
        // TODO: implement, check for errors and use showError(message) in case of error
        if(edge == null){
            showError("Flight is null.");
            return;
        }

        for(Edge<Flight, Airport> e : graph.edges()){
            if(e.equals(edge)){
                graph.removeEdge(e);
                return;
            }
        }
        showError("Flight not found.");
    }

    private void removeAirport(Vertex<Airport> vertex) {
        // TODO: implement, check for errors and use showError(message) in case of error
        if(vertex == null){
            showError("Airport is null.");
            return;
        }

        for(Vertex<Airport> v : graph.vertices()){
            if(v.equals(vertex)){
                graph.removeVertex(v);
                return;
            }
        }
        showError("Airport not found.");
    }


    private Vertex<Airport> getBusiestAirport(){
        int flightCount = -1;
        Vertex<Airport> airport = null;

        if(graph.vertices() == null){
            return null;
        }

        for(Vertex<Airport> v : graph.vertices()){
            if(graph.incidentEdges(v).size() > flightCount){
                flightCount = graph.incidentEdges(v).size();
                airport = v;
            }
        }

        return airport;
    }

    private void updateStatistics() {
        // TODO: query model and update labels
        //  e.g., labelNumberAirports.setText( ?? );

        String nAirports = String.valueOf(graph.vertices().size());
        labelNumberAirports.setText(nAirports);

        String nFlights = String.valueOf(graph.edges().size());
        labelNumberFlights.setText(nFlights);

        if(getBusiestAirport() != null){
            Vertex<Airport> a = getBusiestAirport();
            labelBusiestAirport.setText(a.element().toString());

            String nAirportFlights = String.valueOf(graph.incidentEdges(a).size());
            labelBusiestAirportNumberFlights.setText(nAirportFlights);
        }else {
            labelBusiestAirport.setText("null");
            labelBusiestAirportNumberFlights.setText("null");
        }


    }

    ///////////////////////////////////////////////////////////////////////////////////
    // STUDENTS -> THERE IS NOTHING TO DO BELOW THIS LINE

    public void takeOff() {
        this.graphView.init();
        updateStatistics();
        updateControls();
    }

    private void doLayout() {
        setStyle("-fx-background-color: #FFF;");

        // Top area - user interaction
        VBox top = new VBox(30);
        top.setPadding(new Insets(30));
        HBox firstRow = new HBox(10);
        HBox secondRow = new HBox(10);
        HBox thirdRow = new HBox(10);

        Button buttonAddAirport = new Button("Add");
        Button buttonRemoveAirport = new Button("Remove");
        Button buttonAddFlight = new Button("Add");
        Button buttonRemoveFlight = new Button("Remove");

        this.listAirportsToRemove = FXCollections.observableArrayList();
        this.listAirportsFrom = FXCollections.observableArrayList();
        this.listAirportsTo = FXCollections.observableArrayList();
        this.listFlights = FXCollections.observableArrayList();

        TextField textAirportCode = new TextField();
        textAirportCode.setPromptText("Airport code");
        TextField textFlightCode = new TextField();
        textFlightCode.setPromptText("Flight code");
        TextField textFlightDistance = new TextField();
        textFlightDistance.setPromptText("Flight distance");

        ComboBox<Vertex<Airport>> comboAirportsToRemove = new ComboBox<>(listAirportsToRemove);
        ComboBox<Vertex<Airport>> comboAirportsFrom = new ComboBox<>(listAirportsFrom);
        ComboBox<Vertex<Airport>> comboAirportsTo = new ComboBox<>(listAirportsTo);
        ComboBox<Edge<Flight, Airport>> comboFlightsToRemove = new ComboBox<>(listFlights);

        comboFlightsToRemove.setConverter(new EgdeFlightConverter());
        comboAirportsToRemove.setConverter(new VertexAirportConverter());
        comboAirportsFrom.setConverter(new VertexAirportConverter());
        comboAirportsTo.setConverter(new VertexAirportConverter());

        firstRow.getChildren().addAll(new Label("Add Airport: "), textAirportCode, buttonAddAirport,
                new Separator(Orientation.VERTICAL),
                new Label("Remove Airport: "), comboAirportsToRemove, buttonRemoveAirport);

        secondRow.getChildren().addAll(new Label("Add Flight From: "),
                comboAirportsFrom, new Label("To:"), comboAirportsTo, textFlightCode, textFlightDistance, buttonAddFlight);

        thirdRow.getChildren().addAll(new Label("Remove Flight: "), comboFlightsToRemove, buttonRemoveFlight);
        top.getChildren().addAll(firstRow, secondRow, thirdRow);

        setTop(top);

        // Center area - the graph visualization (airports and flights)
        setCenter(new SmartGraphDemoContainer(this.graphView));

        // Bottom area - statistics
        HBox bottom = new HBox(10);
        bottom.setPadding(new Insets(30));

        Label airportCount = new Label("Airport count: ");
        Label flightCount = new Label("Flight count: ");
        Label busiestAirport = new Label("Busiest Airport: ");
        Label busiestCount = new Label("Busiest Airport Flight Count: ");
        this.labelNumberAirports = new Label("##");
        this.labelNumberFlights = new Label("##");
        this.labelBusiestAirport = new Label("##");
        this.labelBusiestAirportNumberFlights = new Label("##");

        bottom.getChildren().addAll(airportCount, labelNumberAirports,
                flightCount, labelNumberFlights,
                busiestAirport, labelBusiestAirport,
                busiestCount, labelBusiestAirportNumberFlights);

        setBottom(bottom);

        // Bind events
        buttonAddAirport.setOnAction(event -> {
            String airportCode = textAirportCode.getText().trim();
            addAirport(airportCode);
            textAirportCode.clear();
            graphView.update();
            updateControls();
            updateStatistics();
        });

        buttonRemoveAirport.setOnAction(event -> {
            Vertex<Airport> vertex = comboAirportsToRemove.getSelectionModel().getSelectedItem();
            removeAirport(vertex);
            graphView.update();
            updateControls();
            updateStatistics();
        });

        buttonAddFlight.setOnAction(event -> {
            Vertex<Airport> vertexFrom = comboAirportsFrom.getSelectionModel().getSelectedItem();
            Vertex<Airport> vertexTo = comboAirportsTo.getSelectionModel().getSelectedItem();
            String flightCode = textFlightCode.getText();
            String flightDistance = textFlightDistance.getText();
            addFlight(vertexFrom, vertexTo, flightCode, flightDistance);
            textFlightCode.clear();
            textFlightDistance.clear();
            graphView.update();
            updateControls();
            updateStatistics();
        });

        buttonRemoveFlight.setOnAction(event -> {
            Edge<Flight, Airport> edge = comboFlightsToRemove.getSelectionModel().getSelectedItem();
            removeFlight(edge);
            graphView.update();
            updateControls();
            updateStatistics();
        });
    }

    private void updateControls() {
        Collection<Vertex<Airport>> airports = graph.vertices();
        Collection<Edge<Flight, Airport>> flights = graph.edges();

        listAirportsToRemove.clear();
        listAirportsFrom.clear();
        listAirportsTo.clear();

        listAirportsToRemove.addAll(airports);
        listAirportsFrom.addAll(airports);
        listAirportsTo.addAll(airports);

        listFlights.clear();
        listFlights.addAll(flights);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // ERROR REPORTING

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error has occurred. Description below:");
        alert.setContentText(message);

        alert.showAndWait();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // CONVERTERS FOR COMBOBOXES

    private class VertexAirportConverter extends StringConverter<Vertex<Airport>> {

        @Override
        public String toString(Vertex<Airport> vertex) {
            if(vertex == null) return "";
            return vertex.element().toString();
        }

        @Override
        public Vertex<Airport> fromString(String string) {
            return null; // not needed
        }
    }

    private class EgdeFlightConverter extends StringConverter<Edge<Flight, Airport>> {

        @Override
        public String toString(Edge<Flight, Airport> edge) {
            if(edge == null) return "";
            return edge.element().toString();
        }

        @Override
        public Edge<Flight, Airport> fromString(String string) {
            return null; // not needed
        }
    }

}
