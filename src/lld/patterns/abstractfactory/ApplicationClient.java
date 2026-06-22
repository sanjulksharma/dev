package lld.patterns.abstractfactory;

import lld.patterns.abstractfactory.factory.LogisticFactory;
import lld.patterns.abstractfactory.factory.RoadTransportFactory;
import lld.patterns.abstractfactory.product.container.Container;
import lld.patterns.abstractfactory.product.vehicle.TransportVehicle;

import java.util.logging.LoggingPermission;

public class ApplicationClient {


    public static void main(String[] args) {
        LogisticFactory logisticFactory = new RoadTransportFactory();
        Container container = logisticFactory.createContainer();
        TransportVehicle transportVehicle = logisticFactory.createTransportVehicle();
        transportVehicle.transportContainer(container);
    }


}
