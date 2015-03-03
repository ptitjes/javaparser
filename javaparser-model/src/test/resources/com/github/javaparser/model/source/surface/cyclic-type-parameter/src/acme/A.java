package acme;

// Cyclic inheritance involving E
class A<E extends F, F extends G, G extends E> {
}
