package lld.patterns.abstractFactory.factory;

import lld.patterns.abstractFactory.product.container.Container;
import lld.patterns.abstractFactory.product.container.TruckContainer;
import lld.patterns.abstractFactory.product.vehicle.TransportVehicle;
import lld.patterns.abstractFactory.product.vehicle.Truck;

public class RoadTransportFactory implements LogisticFactory {
    @Override
    public TransportVehicle createTransportVehicle() {
        return new Truck();
    }

    @Override
    public Container createContainer() {
        return new TruckContainer();
    }
}
