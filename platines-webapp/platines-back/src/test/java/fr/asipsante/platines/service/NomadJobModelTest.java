/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.nomad.model.Job;

@Order(5)
class NomadJobModelTest {

  @Test
  void testJob() throws IOException {
    String file = Thread.currentThread().getContextClassLoader().getResource("job.json").getFile();
    Reader reader = new FileReader(file);
    ObjectMapper mapper = new ObjectMapper();
    Job job2 = mapper.readValue(reader, Job.class);
    System.out.println(job2);
  }
}
