package com.icthh.xm.ms.balance.config.tenant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.icthh.xm.commons.config.client.repository.TenantConfigRepository;
import com.icthh.xm.commons.config.domain.Configuration;
import com.icthh.xm.commons.gen.model.Tenant;
import com.icthh.xm.commons.migration.db.tenant.provisioner.TenantDatabaseProvisioner;
import com.icthh.xm.commons.tenantendpoint.TenantManager;
import com.icthh.xm.commons.tenantendpoint.provisioner.TenantAbilityCheckerProvisioner;
import com.icthh.xm.commons.tenantendpoint.provisioner.TenantConfigProvisioner;
import com.icthh.xm.commons.tenantendpoint.provisioner.TenantListProvisioner;
import com.icthh.xm.ms.balance.config.ApplicationProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TenantManagerConfigurationUnitTest {

    private TenantManager tenantManager;

    private TenantConfigProvisioner configProvisioner;

    @Spy
    private TenantManagerConfiguration configuration = new TenantManagerConfiguration();

    @Mock
    private TenantAbilityCheckerProvisioner abilityCheckerProvisioner;
    @Mock
    private TenantDatabaseProvisioner databaseProvisioner;
    @Mock
    private TenantListProvisioner tenantListProvisioner;
    @Mock
    private TenantConfigRepository tenantConfigRepository;
    @Mock
    private ApplicationProperties applicationProperties;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(applicationProperties.getSpecificationPathPattern()).thenReturn(
            "/config/tenants/{tenantName}/balance/balancespec.yml");

        configProvisioner = spy(configuration.tenantConfigProvisioner(tenantConfigRepository, applicationProperties));

        tenantManager = configuration.tenantManager(abilityCheckerProvisioner,
                                                    databaseProvisioner,
                                                    configProvisioner,
                                                    tenantListProvisioner);
    }

    @Test
    public void testCreateTenantConfigProvisioning() {

        tenantManager.createTenant(new Tenant().tenantKey("newtenant"));

        List<Configuration> configurations = new ArrayList<>();
        configurations.add(Configuration.of().path("/config/tenants/{tenantName}/balance/balancespec.yml").build());

        verify(tenantConfigRepository).createConfigsFullPath(eq("newtenant"), eq(configurations));

    }

    @Test
    public void testCreateTenantProvisioningOrder() {

        tenantManager.createTenant(new Tenant().tenantKey("newtenant"));

        InOrder inOrder = Mockito.inOrder(abilityCheckerProvisioner,
                                          tenantListProvisioner,
                                          databaseProvisioner,
                                          configProvisioner);

        inOrder.verify(abilityCheckerProvisioner).createTenant(any(Tenant.class));
        inOrder.verify(tenantListProvisioner).createTenant(any(Tenant.class));
        inOrder.verify(configProvisioner).createTenant(any(Tenant.class));
        inOrder.verify(databaseProvisioner).createTenant(any(Tenant.class));

        verifyNoMoreInteractions(abilityCheckerProvisioner,
                                 tenantListProvisioner,
                                 databaseProvisioner,
                                 configProvisioner);
    }

}
