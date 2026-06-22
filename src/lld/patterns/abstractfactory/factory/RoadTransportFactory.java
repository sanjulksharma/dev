package lld.patterns.abstractfactory.factory;

import lld.patterns.abstractfactory.product.container.Container;
import lld.patterns.abstractfactory.product.container.TruckContainer;
import lld.patterns.abstractfactory.product.vehicle.TransportVehicle;
import lld.patterns.abstractfactory.product.vehicle.Truck;

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
