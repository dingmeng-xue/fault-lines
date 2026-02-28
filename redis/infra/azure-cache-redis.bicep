param Redis_my_redis_cache_name string

resource Redis_my_redis_cache_name_resource 'Microsoft.Cache/Redis@2024-11-01' = {
  name: Redis_my_redis_cache_name
  location: 'West US 2'
  properties: {
    redisVersion: '6.0'
    sku: {
      name: 'Standard'
      family: 'C'
      capacity: 1
    }
    enableNonSslPort: true
    minimumTlsVersion: '1.2'
    publicNetworkAccess: 'Enabled'
    redisConfiguration: {
      maxclients: '1000'
      'maxmemory-reserved': '125'
      'maxfragmentationmemory-reserved': '125'
      'maxmemory-delta': '125'
    }
    updateChannel: 'Stable'
    zonalAllocationPolicy: 'Automatic'
    disableAccessKeyAuthentication: false
  }
}

resource Redis_my_redis_cache_name_Data_Contributor 'Microsoft.Cache/Redis/accessPolicies@2024-11-01' = {
  parent: Redis_my_redis_cache_name_resource
  name: 'Data Contributor'
  properties: {
    permissions: '+@all -@dangerous +cluster|info +cluster|nodes +cluster|slots allkeys'
  }
}

resource Redis_my_redis_cache_name_Data_Owner 'Microsoft.Cache/Redis/accessPolicies@2024-11-01' = {
  parent: Redis_my_redis_cache_name_resource
  name: 'Data Owner'
  properties: {
    permissions: '+@all allkeys'
  }
}

resource Redis_my_redis_cache_name_Data_Reader 'Microsoft.Cache/Redis/accessPolicies@2024-11-01' = {
  parent: Redis_my_redis_cache_name_resource
  name: 'Data Reader'
  properties: {
    permissions: '+@read +@connection +cluster|info +cluster|nodes +cluster|slots allkeys'
  }
}

resource Redis_my_redis_cache_name_Redis_my_redis_cache_name_pe_d3d224be_4ed7_49e3_8544_cb90afc68aed 'Microsoft.Cache/Redis/privateEndpointConnections@2024-11-01' = {
  parent: Redis_my_redis_cache_name_resource
  name: '${Redis_my_redis_cache_name}-pe.d3d224be-4ed7-49e3-8544-cb90afc68aed'
  properties: {
    privateEndpoint: {}
    privateLinkServiceConnectionState: {
      status: 'Approved'
      description: 'Auto-Approved'
      actionsRequired: 'None'
    }
  }
}
