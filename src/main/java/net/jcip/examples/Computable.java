package net.jcip.examples;

/**
 * @author Brian Goetz and Tim Peierls
 */
public interface Computable<A, V> {
	V compute(A arg) throws InterruptedException;
}