package lld.patterns.abstractfactory.product.vehicle;

import lld.patterns.abstractfactory.product.container.Container;

public class Ship implements TransportVehicle{

    @Override
    public void transportContainer(Container container) {
        System.out.println(container.getClass().getSimpleName() + " transported");
    }
}
