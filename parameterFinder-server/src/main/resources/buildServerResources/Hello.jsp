<html>
    <head>
        <script language="JavaScript">
            window.onload = loadParameters;

            function loadParameters () {
                var xmlDoc = loadXMLDoc( "/plugins/parameterFinder/parameters.xml" );
                var parameterTags = xmlDoc.getElementsByTagName( "parameter" );

                for ( var index = 0; index < parameterTags.length; index++ ) {
                    [ 'Tool', 'Location', 'File', 'Command', 'Regex' ].each( function ( label ) {
                        document.getElementById( 'params' ).innerHTML += label + ": " + tagValue( parameterTags[ index ], label.toLowerCase() ) + "<br/>";
                    } );
                    document.getElementById( 'params' ).innerHTML += "<br/>";
                }
            }

            function tagValue ( node, tagName ) {
                var returnValue = "";
                var elements = node.getElementsByTagName( tagName );
                if ( elements.length > 0 ) {
                    var firstElement = elements[ 0 ];
                    if ( typeof firstElement === 'object' ) {
                        var childNodes = firstElement.childNodes;
                        if ( childNodes.length > 0 ) {
                            var firstChildNode = childNodes[ 0 ];
                            if ( typeof firstChildNode === 'object' ) {
                                returnValue = firstChildNode.nodeValue
                            }
                        }
                    }

                }
                return returnValue;
            }

            function loadXMLDoc ( filename ) {
                var xhttp = new XMLHttpRequest();
                xhttp.open( "GET", filename, false );
                xhttp.send();
                return xhttp.responseXML;
            }
        </script>
    </head>
    <body>
        <div id="main">
            <div id="params" style="padding:30px;">

            </div>
        </div>
    </body>
</html>