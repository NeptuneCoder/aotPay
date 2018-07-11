package pay;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface IStripeAliPay {
     void pay(final @IntRange(from = 0) long amount,
                    final @NonNull String currency,
                    final @Nullable String name,
                    final @Nullable String email,
                    final @NonNull String returnUrl, final StripeAliPay.OnPayStatusListener listener);
}
