# <h1 align=center>![spreadit-android](https://github.com/eggze/spreadit-android/raw/master/spreadit_logo.png) spreadit-android</h1>
## <h2 align=center>The android client implementation of the <a href="https://spreadit.eggze.com/">spreadit</a></strong> system</h2>

## <h2>What</h2>

<p style="text-align:justify"><strong><a href="https://spreadit.eggze.com/">spreadit</a></strong> is a complete contact tracing solution that is designed to guarantee both your privacy and your data security.</p>

## <h2>Why</h2>

<p style="text-align:justify"><strong><a href="https://spreadit.eggze.com/">spreadit</a></strong> is built to support the <strong><a href="https://www.bag.admin.ch/bag/en/home.html">Federal Office of Public Health</a></strong> efforts to mitigate the spread of COVID-19 through crowdsourced, community-driven <strong>unknown contact tracing</strong> in what we call <em><strong>unsafe locations</strong></em>.</p>

## <h2>How</h2>

<p style="text-align:justify">For an explanation of the data collected and how it is used, please visit the <strong><a href="https://spreadit.eggze.com/privacy-policy/">Privacy Policy</a></strong> section.</p>
		
<p style="text-align:justify">To find out why we chose QR scanning over Bluetooth/BLE or GPS technologies, please visit the <strong><a href="https://spreadit.eggze.com/why-qr">Why QR?</a></strong> section.</p>
		
<p style="text-align:justify">For a detailed description of how the <strong><a href="https://spreadit.eggze.com/">spreadit</a></strong> system is ensuring your anonymity and data privacy, please visit the <strong><a href="https://spreadit.eggze.com/system-design/">System Design</a></strong> section.</p>

## Building from sources

<p style="text-align:justify">This android implementation has been developed with AndroidStudio. To build it, you will also need to create your own server.gradle file. A sample server.gradle looks like the following</p>

```
ext {
    prod = [
            serverUrl   : '"prod.server.com"',
            serverPort   : '1234',
            truststore: '"truststore"',
            truststoreV1: '"truststore_v1"',
            truststorePass: '"truststorePass"',
            truststoreV1Pass: '"truststoreV1Pass"'
    ]
    dev = [
            serverUrl   : '"dev.server.com"',
            serverPort   : '1234',
            truststore: '"truststore"',
            truststoreV1: '"truststore_v1"',
            truststorePass: '"truststorePass"',
            truststoreV1Pass: '"truststoreV1Pass"'
    ]
    debug = [
            serverUrl   : '"debug.server.com"',
            serverPort   : '1234',
            truststore: '"truststore"',
            truststoreV1: '"truststore_v1"',
            truststorePass: '"truststorePass"',
            truststoreV1Pass: '"truststoreV1Pass"'
    ]
}
```

<p style="text-align:justify">To create your own private/public keys for use with your TrustStore and KeyStore implementations, you can either follow the instructions from <strong><a href="https://github.com/dermitza/SecureNIO">SecureNIO</a></strong> or alternatively you can also use any tool, such as <strong><a href="https://sourceforge.net/projects/portecle/">Portecle</a></strong></p>

## License

GNU AFFERO GENERAL PUBLIC LICENSE Version 3

Copyright (C) 2020 eggze Technik GmbH
 
spreadit-android is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

spreadit-android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License along with spreadit-android. If not, see <http://www.gnu.org/licenses/>.