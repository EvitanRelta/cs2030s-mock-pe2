package cs2030s.fp;

/**
 * Monad for try-catching.
 *
 * @version CS2030S PE2 Question 1, AY20/21 Semester 2
 * @author A0235143N
 * @param <T> Type of the value contained in the Try, returned by 'get()'.
 */
public abstract class Try<T> {
  protected abstract boolean isSucess();

  public abstract T get() throws Throwable;

  public abstract <U> Try<U> map(Transformer<? super T, ? extends U> transformer);

  public abstract <U> Try<U> flatMap(
      Transformer<? super T, ? extends Try<? extends U>> transformer
  );

  /**
   * Passes Throwable to 'consumer' param upon failure. If 'consumer' doesn't
   * throw any errors, return the same failed Try. Else, return a new failed
   * Try with the newly thrown Throwable.
   *
   * @param consumer Consumer that takes in the failure's Throwable.
   * @return The same Try instance, or a new failed Try (if consumer throws a Throwable).
   */
  public abstract Try<T> onFailure(Consumer<? super Throwable> consumer);


  /**
   * Attempt to recover from a failed Try. Passes the failed Try's Throwable
   * to 'transformer' to get a new value. If 'transformer' throws a Throwable,
   * return another failed Try with the new Throwable.
   *
   * @param transformer Transformer that transforms the failed Throwable to a new value.
   * @return A recovered Try, or a new failed Try (if transformer fails).
   */
  public abstract Try<T> recover(Transformer<? super Throwable, ? extends T> transformer);
  
  /**
   * Factory method for initialising a Try.
   *
   * @param <T> Type of the value in the returned Try.
   * @param producer Producer that produces the value in the Try.
   * @return The initialised Try instance.
   */
  public static <T> Try<T> of(Producer<? extends T> producer) {
    try {
      return Try.success(producer.produce());
    } catch (Throwable e) {
      return Try.failure(e);
    }
  }

  /**
   * Initialises a successful Try.
   *
   * @param <T> Type of the value in the returned Try.
   * @param value The value stored in the returned Try.
   * @return The sucessful Try.
   */
  public static <T> Try<T> success(T value) {
    return new Success<>(value);
  }

  /**
   * Initialises a failed Try.
   *
   * @param <T> Type of the value in the returned Try.
   * @param throwable The Throwable that caused the failure of the Try.
   * @return The failed Try.
   */
  public static <T> Try<T> failure(Throwable throwable) {
    // Failure doesn't have any value inside, thus its safe to typecast.
    @SuppressWarnings("unchecked")
    Try<T> output = (Try<T>) new Failure(throwable);
    return output;
  }


  // ===================== Nested Classes ====================================

  private static class Failure extends Try<Object> {
    private final Throwable throwable;

    private Failure(Throwable throwable) {
      this.throwable = throwable;
    }

    public Object get() throws Throwable {
      throw this.throwable;
    }

    @Override
    protected boolean isSucess() {
      return false;
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Failure)) {
        return false;
      }

      Failure other = (Failure) obj;

      return String.valueOf(this.throwable) 
          == String.valueOf(other.throwable);
    }

    @Override
    public <U> Try<U> map(Transformer<? super Object, ? extends U> transformer) {
      @SuppressWarnings("unchecked")
      Try<U> output = (Try<U>) this;
      return output;
    }

    @Override
    public <U> Try<U> flatMap(Transformer<? super Object, ? extends Try<? extends U>> transformer) {
      @SuppressWarnings("unchecked")
      Try<U> output = (Try<U>) this;
      return output;
    }

    /**
     * Passes Throwable to 'consumer' param upon failure. If 'consumer' doesn't
     * throw any errors, return the same failed Try. Else, return a new failed
     * Try with the newly thrown Throwable.
     *
     * @param consumer Consumer that takes in the failure's Throwable.
     * @return The same Try instance, or a new failed Try (if consumer throws a Throwable).
     */
    @Override
    public Try<Object> onFailure(Consumer<? super Throwable> consumer) {
      try {
        consumer.consume(this.throwable);
        return this;
      } catch (Throwable e) {
        return Try.failure(e);
      }
    }

    /**
     * Attempt to recover from a failed Try. Passes the failed Try's Throwable
     * to 'transformer' to get a new value. If 'transformer' throws a Throwable,
     * return another failed Try with the new Throwable.
     *
     * @param transformer Transformer that transforms the failed Throwable to a new value.
     * @return A recovered Try, or a new failed Try (if transformer fails).
     */
    @Override
    public Try<Object> recover(Transformer<? super Throwable, ? extends Object> transformer) {
      try {
        Object newValue = transformer.transform(this.throwable);
        return Try.success(newValue);
      } catch (Throwable e) {
        return Try.failure(e);
      }
    }
  }

  private static class Success<T> extends Try<T> {
    private final T value;

    public Success(T value) {
      this.value = value;
    }

    @Override
    public T get() {
      return this.value;
    }

    @Override
    protected boolean isSucess() {
      return true;
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Success<?>)) {
        return false;
      }

      Success<?> other = (Success<?>) obj;

      return this.value == null
          ? this.value == other.value
          : this.value.equals(other.value);
    }

    @Override
    public <U> Try<U> map(Transformer<? super T, ? extends U> transformer) {
      try {
        U newValue = transformer.transform(this.value);
        return Try.success(newValue);
      } catch (Throwable e) {
        return Try.failure(e);
      }
    }

    @Override
    public <U> Try<U> flatMap(Transformer<? super T, ? extends Try<? extends U>> transformer) {
      try {
        U newValue = transformer.transform(this.value).get();
        return Try.success(newValue);
      } catch (Throwable e) {
        return Try.failure(e);
      }
    }

    /**
     * Does nothing on successful Try.
     *
     * @param consumer Consumer that takes in the failure's Throwable.
     * @return The same Try instance.
     */
    @Override
    public Try<T> onFailure(Consumer<? super Throwable> consumer) {
      return this;
    }

    /**
     * Does nothing on successful Try.
     *
     * @param transformer Transformer that takes in the failure's Throwable.
     * @return The same Try instance.
     */
    @Override
    public Try<T> recover(Transformer<? super Throwable, ? extends T> transformer) {
      return this;
    }
  }
}
