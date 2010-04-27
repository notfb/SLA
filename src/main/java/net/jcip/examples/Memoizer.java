package net.jcip.examples;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Memoizer
 * <p/>
 * Final implementation of Memoizer. See
 * http://www.javaconcurrencyinpractice.com .
 * <p/>
 * Modified to support a computable for hashing.
 * 
 * @author Brian Goetz and Tim Peierls
 */
public class Memoizer<A, V, H> implements Computable<A, V> {

	private final ConcurrentMap<H, Future<V>> cache;
	protected final Computable<A, V> c; // anon class needs access
	private final Computable<A, H> hash;

	public Memoizer(Computable<A, V> c, Computable<A, H> hash) {
		cache = new ConcurrentHashMap<H, Future<V>>();
		this.c = c;
		this.hash = hash;
	}

	public Memoizer(Computable<A, V> c, Computable<A, H> hash, int capacity,
			float loadFactor, int concurrencyLevel) {
		cache = new ConcurrentHashMap<H, Future<V>>(capacity, loadFactor,
				concurrencyLevel);
		this.c = c;
		this.hash = hash;
	}

	public V compute(final A arg) throws InterruptedException {
		while (true) {
			Future<V> f = cache.get(arg);
			if (f == null) {
				Callable<V> eval = new Callable<V>() {
					public V call() throws InterruptedException {
						return c.compute(arg);
					}
				};
				FutureTask<V> ft = new FutureTask<V>(eval);
				f = cache.putIfAbsent(hash.compute(arg), ft);
				if (f == null) {
					f = ft;
					ft.run();
				}
			}
			try {
				return f.get();
			} catch (CancellationException e) {
				cache.remove(arg, f);
			} catch (ExecutionException e) {
				throw LaunderThrowable.launderThrowable(e.getCause());
			}
		}
	}
}