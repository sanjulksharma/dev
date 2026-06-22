package lld.patterns.abstractfactory.factory;

import lld.patterns.abstractfactory.product.container.Container;
import lld.patterns.abstractfactory.product.vehicle.TransportVehicle;

public interface LogisticFactory {

    TransportVehicle createTransportVehicle();
    Container createContainer();
}
