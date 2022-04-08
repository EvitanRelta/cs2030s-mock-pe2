package cs2030s.fp;

/**
 * Monad for try-catching.
 *
 * @version CS2030S PE2 Question 1, AY20/21 Semester 2
 * @author A0235143N
 * @param <T> Type of the value contained in the Try, returned by 'get()'.
 */
public abstract class Try<T> {
  private Try() {

  }

  protected abstract boolean isSucess();

  public abstract T get() throws Throwable;

  public abstract <U> Try<U> map(Transformer<? super T, ? extends U> transformer);

  public abstract <U> Try<U> flatMap(
      Transformer<? super T, ? extends Try<? extends U>> transformer
  );

  /**
   * If Try succeeds, do nothing. Else passes the failed Try's Throwable to 
   * 'consumer' param. If 'consumer' doesn't throw any errors, return the same 
   * failed Try. Else, return a new failed Try with the newly thrown Throwable.
   *
   * @param consumer Consumer that takes in the failure's Throwable.
   * @return The same Try instance, or a new failed Try (if consumer throws a Throwable).
   */
  public abstract Try<T> onFailure(Consumer<? super Throwable> consumer);


  /**
   * If Try succeeds, do nothing. Else attempt to recover from a failed Try,
   * by passing the failed Try's Throwable to 'transformer' to get a new value. 
   * If 'transformer' throws a Throwable, return another failed Try with the 
   * new Throwable; else return a successful Try with the new value.
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
    return new Failure<>(throwable);
  }


  // ===================== Nested Classes ====================================

  private static class Failure<T> extends Try<T> {
    private final Throwable throwable;

    private Failure(Throwable throwable) {
      this.throwable = throwable;
    }

    public T get() throws Throwable {
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

      Failure<?> other = (Failure<?>) obj;

      return String.valueOf(this.throwable) 
          == String.valueOf(other.throwable);
    }

    @Override
    public <U> Try<U> map(Transformer<? super T, ? extends U> transformer) {
      @SuppressWarnings("unchecked")
      Try<U> output = (Try<U>) this;
      return output;
    }

    @Override
    public <U> Try<U> flatMap(Transformer<? super T, ? extends Try<? extends U>> transformer) {
      @SuppressWarnings("unchecked")
      Try<U> output = (Try<U>) this;
      return output;
    }

    @Override
    public Try<T> onFailure(Consumer<? super Throwable> consumer) {
      try {
        consumer.consume(this.throwable);
        return this;
      } catch (Throwable e) {
        return Try.failure(e);
      }
    }
    
    @Override
    public Try<T> recover(Transformer<? super Throwable, ? extends T> transformer) {
      try {
        T newValue = transformer.transform(this.throwable);
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
    
    @Override
    public Try<T> onFailure(Consumer<? super Throwable> consumer) {
      return this;
    }
    
    @Override
    public Try<T> recover(Transformer<? super Throwable, ? extends T> transformer) {
      return this;
    }
  }
}
