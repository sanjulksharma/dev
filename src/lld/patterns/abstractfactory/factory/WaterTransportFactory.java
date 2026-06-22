package lld.patterns.abstractfactory.factory;

import lld.patterns.abstractfactory.product.container.Container;
import lld.patterns.abstractfactory.product.container.ShipContainer;
import lld.patterns.abstractfactory.product.vehicle.Ship;
import lld.patterns.abstractfactory.product.vehicle.TransportVehicle;

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
