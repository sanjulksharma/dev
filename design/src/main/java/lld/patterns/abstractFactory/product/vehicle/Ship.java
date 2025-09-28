package lld.patterns.abstractFactory.product.vehicle;

import lld.patterns.abstractFactory.product.container.Container;

public class Ship implements TransportVehicle{

    @Override
    public void transportContainer(Container container) {
        System.out.println(container.getClass().getSimpleName() + " transported");
    }
}
