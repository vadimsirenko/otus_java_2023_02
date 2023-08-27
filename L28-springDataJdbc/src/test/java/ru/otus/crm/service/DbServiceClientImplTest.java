package ru.otus.crm.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.ClientDetails;
import ru.otus.crm.model.Manager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
@Testcontainers
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(initializers = {DbServiceClientImplTest.Initializer.class})
@ActiveProfiles("test")
class DbServiceClientImplTest {
    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13");
    @Autowired
    DBServiceClient dbServiceClient;
    @Autowired
    DBServiceManager dbServiceManager;

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Test
    void saveClient() {
        List<Manager> managers = dbServiceManager.findByLabel("Manager 2");
        Client client = new Client("Vadim", managers.get(0).getId(), 1, new ClientDetails("Vadim info"));
        Client savedClient = dbServiceClient.saveClient(client);

        assertEquals("Vadim", savedClient.getName());
        assertEquals("Vadim info", savedClient.getClientInfo().getInfo());
        assertEquals(managers.get(0).getId(), savedClient.getManagerId());
        assertEquals(1, savedClient.getOrderColumn());
        assertNotNull(savedClient.getId());
    }

    @Test
    void getClient() {
        Optional<Client> readedClientOpt = dbServiceClient.getClient(1L);

        assertTrue(readedClientOpt.isPresent());
        Client readedClient = readedClientOpt.get();

        assertEquals("Vasya", readedClient.getName());
        assertEquals("Vasya info", readedClient.getClientInfo().getInfo());
        assertEquals("mgr-1", readedClient.getManagerId());
        assertEquals(1, readedClient.getOrderColumn());
        assertNotNull(readedClient.getId());
    }

    @Test
    void findAll() {
        List<Client> clients = dbServiceClient.findAll();
        assertEquals(4, clients.size());
    }
}