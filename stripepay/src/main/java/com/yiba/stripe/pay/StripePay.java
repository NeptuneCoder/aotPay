package com.yiba.stripe.pay;

import android.app.Activity;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.stripe.android.Stripe;
import com.stripe.android.exception.APIConnectionException;
import com.stripe.android.exception.APIException;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.exception.InvalidRequestException;
import com.stripe.android.model.Source;
import com.stripe.android.model.SourceParams;

import java.util.Map;

public class StripePay {
    private final Activity activity;
    private final String publicKey;

    public StripePay(Activity activity, String publicKey) {
        this.activity = activity;
        this.publicKey = publicKey;
    }

    public Source generateKey(@IntRange(from = 0) long amount, @NonNull String currency, @Nullable String name, @Nullable String email, @NonNull String returnUrl) throws APIException, AuthenticationException, InvalidRequestException, APIConnectionException {
        /**
         *  @IntRange(from = 0) long amount,
         @NonNull String currency,
         @Nullable String name,
         @Nullable String email,
         @NonNull String returnUrl
         */
        final SourceParams sourceParams = SourceParams.createAlipaySingleUseParams(amount, currency, name, email, returnUrl);
        final Stripe stripe = new Stripe(activity);
        Source source = null;
        source = stripe.createSourceSynchronous(sourceParams, publicKey);
        return source;

    }
}
