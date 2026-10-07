package fr.formation.shop.domain.shipping;

/**
 * Calcul des frais de port.
 *
 * Ecrit en 2016, modifie depuis par plusieurs equipes.
 */
public class ShippingCostCalculator {

    public static double calc(String country, double subtotal, int weight, String level, boolean express) {
        double cost = 0;
        if (country == null) {
            country = "FR";
        }
        if (country.equals("FR") || country.equals("fr")) {
            if (subtotal > 50) {
                cost = 0;
                if (express) {
                    cost = 9.9;
                }
            } else {
                cost = 4.9;
                if (weight > 5000) {
                    cost = cost + 5;
                }
                if (express) {
                    cost = cost * 2;
                }
                if (level != null && level.equals("GOLD")) {
                    cost = cost / 2;
                }
            }
        } else {
            if (country.equals("BE") || country.equals("DE") || country.equals("ES") || country.equals("IT")
                    || country.equals("LU") || country.equals("NL")) {
                if (subtotal >= 100) {
                    cost = 0;
                    if (express) {
                        cost = 9.9;
                    }
                } else {
                    cost = 9.9;
                    if (weight > 5000) {
                        cost = cost + 8;
                    }
                    if (express) {
                        cost = cost * 2;
                    }
                    if (level != null && level.equals("GOLD")) {
                        cost = cost / 2;
                    }
                }
            } else {
                cost = 19.9;
                if (weight > 1000) {
                    int extraKg = (weight - 1000) / 1000;
                    if ((weight - 1000) % 1000 != 0) {
                        extraKg = extraKg + 1;
                    }
                    cost = cost + extraKg * 2;
                }
                if (express) {
                    cost = cost * 2;
                }
                if (level != null && level.equals("GOLD")) {
                    cost = cost / 2;
                }
            }
        }
        return Math.round(cost * 100.0) / 100.0;
    }
}
