package lld.patterns.abstractFactory.factory;

import lld.patterns.abstractFactory.product.container.Container;
import lld.patterns.abstractFactory.product.container.ShipContainer;
import lld.patterns.abstractFactory.product.vehicle.Ship;
import lld.patterns.abstractFactory.product.vehicle.TransportVehicle;

public class WaterTransportFactory implements LogisticFactory {
    @Override
    public TransportVehicle createTransportVehicle() {
        return new Ship();
    }

    @Override
    public Container createContainer() {
        return new ShipContainer();
    }
}
