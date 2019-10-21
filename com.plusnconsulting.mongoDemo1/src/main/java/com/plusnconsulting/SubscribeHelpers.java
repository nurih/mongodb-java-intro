/*
 * Copyright 2015 MongoDB, Inc.
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

package com.plusnconsulting;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.mongodb.MongoTimeoutException;

import org.bson.Document;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 *  Subscribe helper implementations for the Quick Tour.
 */
public final class SubscribeHelpers {

    /**
     * A Subscriber that stores the publishers results and provides a latch so can block on completion.
     *
     * @param <T> The publishers result type
     */
    public static class ObservableSubscriber<T> implements Subscriber<T> {
        private final List<T> items;
        private final List<Throwable> errors;
        private final CountDownLatch latch;
        private volatile Subscription subscription;
        private volatile boolean completed;

        ObservableSubscriber() {
            this.items = new ArrayList<T>();
            this.errors = new ArrayList<Throwable>();
            this.latch = new CountDownLatch(1);
        }

        @Override
        public void onSubscribe(final Subscription s) {
            subscription = s;
        }

        @Override
        public void onNext(final T t) {
            items.add(t);
        }

        @Override
        public void onError(final Throwable t) {
            errors.add(t);
            onComplete();
        }

        @Override
        public void onComplete() {
            completed = true;
            latch.countDown();
        }

        public Subscription getSubscription() {
            return subscription;
        }

        public List<T> getItems() {
            return items;
        }

        public Throwable getError() {
            if (errors.size() > 0) {
                return errors.get(0);
            }
            return null;
        }

        public boolean isCompleted() {
            return completed;
        }

        public List<T> get(final long timeout, final TimeUnit unit) throws Throwable {
            return await(timeout, unit).getItems();
        }

        public ObservableSubscriber<T> await() throws Throwable {
            return await(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }

        public ObservableSubscriber<T> await(final long timeout, final TimeUnit unit) throws Throwable {
            subscription.request(Integer.MAX_VALUE);
            if (!latch.await(timeout, unit)) {
                throw new MongoTimeoutException("Publisher onComplete timed out");
            }
            if (!errors.isEmpty()) {
                throw errors.get(0);
            }
            return this;
        }
    }

    /**
     * A Subscriber that immediately requests Integer.MAX_VALUE onSubscribe
     *
     * @param <T> The publishers result type
     */
    public static class InvokingSubscriber<T> extends ObservableSubscriber<T> {

        @Override
        public void onSubscribe(final Subscription s) {
            super.onSubscribe(s);
            s.request(Integer.MAX_VALUE);
        }
    }

    /**
     * A Subscriber that prints a message including the received items on completion
     *
     * @param <T> The publishers result type
     */
    public static class PrintSubscriber<T> extends InvokingSubscriber<T> {
        private final String message;

        /**
         * A Subscriber that outputs a message onComplete.
         *
         * @param message the message to output onComplete
         */
        public PrintSubscriber(final String message) {
            this.message = message;
        }

        @Override
        public void onComplete() {
            System.out.println(format(message, getItems()));
            super.onComplete();
        }
    }

    /**
     * A Subscriber that prints the json version of each document
     */
    public static class PrintDocumentSubscriber extends InvokingSubscriber<Document> {

        @Override
        public void onNext(final Document document) {
            super.onNext(document);
            System.out.println(document.toJson());
        }
    }

    private SubscribeHelpers() {
    }
}
