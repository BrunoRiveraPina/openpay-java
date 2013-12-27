/*
 * Copyright 2013 Opencard Inc.
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
package mx.openpay.core.client;

import static mx.openpay.core.client.TestConstans.API_KEY;
import static mx.openpay.core.client.TestConstans.ENDPOINT;
import static mx.openpay.core.client.TestConstans.MERCHANT_ID;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import lombok.extern.slf4j.Slf4j;
import mx.openpay.client.Address;
import mx.openpay.client.Card;
import mx.openpay.client.Subscription;
import mx.openpay.client.core.OpenpayAPI;
import mx.openpay.client.core.operations.SubscriptionOperations;

import org.junit.Before;
import org.junit.Test;

/**
 * @author elopez
 */
@Slf4j
public class SubscriptionOperationsTest {

    private static final String TRIAL_PLAN_ID = "pqycd8nndru5jeav5lh7";

    private static final String NO_TRIAL_PLAN_ID = "ppnfut1d0yuyflei0pfx";

    private static final String EXISTING_SUBSCRIPTION_ID = "sa8svmpy0alpoko3g7bl";

    private static final String UPDATE_SUBSCRIPTION_ID = "soes8nwf4bdscnpi5bru";

    private OpenpayAPI api;

    private SubscriptionOperations subscriptions;

    @Before
    public void setUp() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("Mexico/General"));
        this.api = new OpenpayAPI(ENDPOINT, API_KEY, MERCHANT_ID);
        this.subscriptions = this.api.subscriptions();
    }

    @Test
    public void testCreate_Trial() throws Exception {
        Card card = this.getCard();
        Subscription createSubscription = new Subscription()
                .planId(TRIAL_PLAN_ID)
                .card(card);
        Subscription subscription = this.subscriptions.create(TestConstans.CUSTOMER_ID, createSubscription);
        this.subscriptions.delete(TestConstans.CUSTOMER_ID, subscription.getId());
        log.info("{}", subscription);
        assertNotNull(subscription.getId());
        assertNull(subscription.getCard().getId());
        assertThat(subscription.getCard().getCardNumber(), is("2033"));
        assertNotNull(subscription.getCreationDate());
        assertNotNull(subscription.getChargeDate());
        assertThat(subscription.getCurrentPeriodNumber(), is(0));
        assertThat(subscription.getCustomerId(), is(TestConstans.CUSTOMER_ID));
        assertNotNull(subscription.getPeriodEndDate());
        assertThat(subscription.getPlanId(), is(TRIAL_PLAN_ID));
        assertThat(subscription.getStatus(), is("trial"));
        assertNotNull(subscription.getTrialEndDate());
    }

    @Test
    public void testCreate_NoTrial() throws Exception {
        Card card = this.getCard();
        Subscription createSubscription = new Subscription()
                .planId(NO_TRIAL_PLAN_ID).card(card);
        Subscription subscription = this.subscriptions.create(TestConstans.CUSTOMER_ID, createSubscription);
        this.subscriptions.delete(TestConstans.CUSTOMER_ID, subscription.getId());
        log.info("{}", subscription);
        assertNotNull(subscription.getId());
        assertNull(subscription.getCard().getId());
        assertThat(subscription.getCard().getCardNumber(), is("2033"));
        assertNotNull(subscription.getCreationDate());
        assertNotNull(subscription.getChargeDate());
        assertThat(subscription.getCurrentPeriodNumber(), is(1));
        assertThat(subscription.getCustomerId(), is(TestConstans.CUSTOMER_ID));
        assertNotNull(subscription.getPeriodEndDate());
        assertThat(subscription.getPlanId(), is(NO_TRIAL_PLAN_ID));
        assertThat(subscription.getStatus(), is("active"));
        assertNull(subscription.getTrialEndDate());
    }

    @Test
    public void testGet() throws Exception {
        Subscription subscription = this.subscriptions.get(TestConstans.CUSTOMER_ID, EXISTING_SUBSCRIPTION_ID);
        log.info("{}", subscription);
        assertThat(subscription.getId(), is(EXISTING_SUBSCRIPTION_ID));
        assertNull(subscription.getCard().getId());
        assertThat(subscription.getCard().getCardNumber(), is("1881"));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse("2013-12-26 10:53:04");
        assertThat(subscription.getCreationDate(), is(date));
        Date chargeDate = simpleDateFormat.parse("2014-08-26 00:00:00");
        assertThat(subscription.getChargeDate(), is(chargeDate));
        assertThat(subscription.getCurrentPeriodNumber(), is(0));
        assertThat(subscription.getCustomerId(), is(TestConstans.CUSTOMER_ID));
        Date periodEndDate = simpleDateFormat.parse("2014-08-25 00:00:00");
        assertThat(subscription.getPeriodEndDate(), is(periodEndDate));
        assertThat(subscription.getPlanId(), is("pqycd8nndru5jeav5lh7"));
        assertThat(subscription.getStatus(), is("trial"));
        Date trialEndDate = simpleDateFormat.parse("2014-08-25 00:00:00");
        assertThat(subscription.getTrialEndDate(), is(trialEndDate));
    }

    @Test
    public void testUpdate() throws Exception {


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date trialEndDate = simpleDateFormat.parse("2015-03-16 01:12:55");
        Date trialEndDateNoMinutes = simpleDateFormat.parse("2015-03-16 00:00:00");
        Subscription subscription = this.subscriptions.get(TestConstans.CUSTOMER_ID, UPDATE_SUBSCRIPTION_ID);

        subscription.setTrialEndDate(trialEndDate);
        subscription.setCancelAtPeriodEnd(true);
        subscription.setCard(this.getCard());

        subscription = this.subscriptions.update(subscription);

        assertThat(subscription.getId(), is(UPDATE_SUBSCRIPTION_ID));
        assertNull(subscription.getCardId());
        assertNull(subscription.getCard().getId());
        assertThat(subscription.getCard().getCardNumber(), is("2033"));
        assertThat(subscription.getTrialEndDate(), is(trialEndDateNoMinutes));
        assertThat(subscription.getCancelAtPeriodEnd(), is(true));

        trialEndDate = simpleDateFormat.parse("2017-05-21 06:12:55");
        trialEndDateNoMinutes = simpleDateFormat.parse("2017-05-21 00:00:00");
        subscription.setTrialEndDate(trialEndDate);
        subscription = this.subscriptions.update(subscription);

        assertThat(subscription.getId(), is(UPDATE_SUBSCRIPTION_ID));
        assertNull(subscription.getCardId());
        assertNull(subscription.getCard().getId());
        assertThat(subscription.getCard().getCardNumber(), is("2033"));
        assertThat(subscription.getTrialEndDate(), is(trialEndDateNoMinutes));
        assertThat(subscription.getCancelAtPeriodEnd(), is(true));

        subscription.setCancelAtPeriodEnd(false);
        subscription = this.subscriptions.update(subscription);

        assertThat(subscription.getId(), is(UPDATE_SUBSCRIPTION_ID));
        assertNull(subscription.getCardId());
        assertNull(subscription.getCard().getId());
        assertThat(subscription.getCard().getCardNumber(), is("2033"));
        assertThat(subscription.getTrialEndDate(), is(trialEndDateNoMinutes));
        assertThat(subscription.getCancelAtPeriodEnd(), is(false));

        subscription.setCardId("kerxkwvyldyzcw05pv7k");
        subscription.setCard(null);
        subscription = this.subscriptions.update(subscription);

        assertThat(subscription.getId(), is(UPDATE_SUBSCRIPTION_ID));
        assertThat(subscription.getCardId(), is("kerxkwvyldyzcw05pv7k"));
        assertThat(subscription.getCard().getId(), is("kerxkwvyldyzcw05pv7k"));
        assertThat(subscription.getCard().getCardNumber(), is("1883"));
        assertThat(subscription.getTrialEndDate(), is(trialEndDateNoMinutes));
        assertThat(subscription.getCancelAtPeriodEnd(), is(false));
    }

    @Test
    public void testList() throws Exception {
        List<Subscription> subscription = this.subscriptions.list(TestConstans.CUSTOMER_ID, null);
        assertTrue(subscription.size() > 0);
    }

    private Card getCard() {
        Address address = this.createAddress();
        Card card = new Card()
                .cardNumber("5243385358972033")
                .holderName("Holder")
                .expirationMonth(12)
                .expirationYear(15)
                .cvv2("123")
                .address(address);
        return card;
    }

    private Address createAddress() {
        return new Address()
                .city("Querétaro")
                .line1("Camino #11 - 01")
                .postalCode("76090")
                .state("Queretaro")
                .countryCode("MX");
    }
}
