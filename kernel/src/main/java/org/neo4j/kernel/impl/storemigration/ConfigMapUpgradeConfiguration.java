/**
 * Copyright (c) 2002-2011 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.impl.storemigration;

import java.util.Map;

import org.neo4j.kernel.Config;

public class ConfigMapUpgradeConfiguration implements UpgradeConfiguration
{
    private Map<?, ?> config;

    public ConfigMapUpgradeConfiguration( Map<?, ?> config )
    {
        this.config = config;
    }

    @Override
    public void checkConfigurationAllowsAutomaticUpgrade()
    {
        String allowUpgrade = (String) config.get( Config.ALLOW_STORE_UPGRADE );
        if ( !Boolean.parseBoolean( allowUpgrade ) )
        {
            throw new UpgradeNotAllowedByConfigurationException(
                    String.format(
                            "Failed to start Neo4j with an older data store version. "
                                    + "To enable automatic upgrade, please set configuration parameter \"%s=true\"",
                            Config.ALLOW_STORE_UPGRADE ) );
        }
    }

}
