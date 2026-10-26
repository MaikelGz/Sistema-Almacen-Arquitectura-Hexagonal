package com.acme.inventory.domain.utils;

/**
 * Utilidades para la generación de identificadores únicos de inventario.
 */
public final class IdUtils {

    private IdUtils() {
        // Previene la instanciación
    }

    /**
     * Construye el identificador único de un ítem a partir de SKU y almacén.
     *
     * @param sku       El SKU del ítem.
     * @param warehouse El identificador del almacén.
     * @return El identificador único en formato "sku::warehouse".
     */
    public static String idOf(String sku, String warehouse) {
        return sku + "::" + warehouse;
    }
}
