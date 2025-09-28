package lld.patterns.abstractFactory.factory;

import lld.patterns.abstractFactory.product.container.Container;
import lld.patterns.abstractFactory.product.vehicle.TransportVehicle;

public interface LogisticFactory {

    TransportVehicle createTransportVehicle();
    Container createContainer();
}
