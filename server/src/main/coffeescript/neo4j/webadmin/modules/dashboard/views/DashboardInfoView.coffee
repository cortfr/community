###
Copyright (c) 2002-2011 "Neo Technology,"
Network Engine for Objects in Lund AB [http://neotechnology.com]

This file is part of Neo4j.

Neo4j is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
###

define( 
  ['./info',
   'ribcage/View',
   'ribcage/ui/NumberFormatter',
   'lib/backbone'], 
  (template,View, NumberFormatter) ->
  
    class DashboardInfoView extends View
      
      template : template
     
      initialize : (opts) =>
        @primitives = opts.primitives
        @diskUsage = opts.diskUsage
        @cacheUsage = opts.cacheUsage
        
        @primitives.bind("change",@render)
        @diskUsage.bind("change",@render)
        @cacheUsage.bind("change",@render)

      render : =>
        $(@el).html @template
          primitives  : @primitives
          diskUsage   : @diskUsage
          cacheUsage  : @cacheUsage
          fancyNumber : NumberFormatter.fancy
        return this

      remove : =>
        @primitives.unbind("change",@render)
        @diskUsage.unbind("change",@render)
        @cacheUsage.unbind("change",@render)
        super()
)
