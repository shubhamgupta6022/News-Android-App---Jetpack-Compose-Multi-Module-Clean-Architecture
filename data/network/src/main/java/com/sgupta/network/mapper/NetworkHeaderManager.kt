package com.sgupta.network.mapper

import com.sgupta.core.Mapper
import com.sgupta.network.client.NetworkHost
import com.sgupta.network.header.HeaderMap

interface NetworkHeaderManager : Mapper<NetworkHost, HeaderMap>