/*
 * Copyright 2014 Opencard Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mx.openpay.core.client.full;

import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import mx.openpay.client.Charge;
import mx.openpay.client.core.requests.transactions.CreateBitcoinChargeParams;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Oswaldo Martinez oswaldo.martinez@openpay.mx
 */
public class MerchantBitcoinChargesTest extends BaseTest {

    @Test
    public void testCreate_Merchant_Store() throws Exception {
        BigDecimal amount = new BigDecimal("100.00");
        String desc = "Pago de boleto";
        String orderId = String.valueOf(System.currentTimeMillis());
        Charge transaction = this.api.charges().create(new CreateBitcoinChargeParams().amount(amount).description(desc)
                .orderId(orderId));
        assertNotNull(transaction);
        assertNotNull(transaction.getPaymentMethod().getReference());
        assertNotNull(transaction.getPaymentMethod().getBarcodeUrl());
        Assert.assertNull(transaction.getFee());
    }

}
