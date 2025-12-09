/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.listener.publisher;

import fr.asipsante.listener.entity.ServerOperation;

public interface MockResultsPublisher {

  void publish(ServerOperation history);
}
