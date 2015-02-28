package edgeconvert;

import edgeconvert.view.DefineRelationsView;
import edgeconvert.view.DefineTablesView;

public class RunEdgeConvert {
   public static void main(String[] args) {
       
       // Create the mediator and instantiate the views
       EdgeConvertMediator mediator = EdgeConvertMediator.getInstance();
       mediator.setDefineTablesView(new DefineTablesView());
       mediator.setDefineRelationsView(new DefineRelationsView());
       mediator.showDefineTablesView(); // show initial view
   }
}