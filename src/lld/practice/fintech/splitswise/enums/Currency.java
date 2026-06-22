package lld.practice.fintech.splitswise.enums;

/**
 * Supported currencies.
 *
 * <p>The minor-unit count lets us store every amount as an integer of minor units
 * (paise / cents) and avoid floating-point error in splits and rounding.
 */
public enum Currency {
    INR(2),
    USD(2),
    EUR(2),
    JPY(0);

    private final int minorUnitDigits;

    Currency(int minorUnitDigits) {
        this.minorUnitDigits = minorUnitDigits;
    }

    public int getMinorUnitDigits() {
        return minorUnitDigits;
    }
}
