package fr.asipsante.listener.publisher;

import fr.asipsante.listener.entity.ServerOperation;

public interface IPublisher {

    void publish(ServerOperation history);
}
