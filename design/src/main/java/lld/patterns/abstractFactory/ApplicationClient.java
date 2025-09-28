package lld.patterns.abstractFactory;

import lld.patterns.abstractFactory.factory.LogisticFactory;
import lld.patterns.abstractFactory.factory.RoadTransportFactory;
import lld.patterns.abstractFactory.product.container.Container;
import lld.patterns.abstractFactory.product.vehicle.TransportVehicle;

import java.util.logging.LoggingPermission;

public class ApplicationClient {


    public static void main(String[] args) {
        LogisticFactory logisticFactory = new RoadTransportFactory();
        Container container = logisticFactory.createContainer();
        TransportVehicle transportVehicle = logisticFactory.createTransportVehicle();
        transportVehicle.transportContainer(container);
    }


}
