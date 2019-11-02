#Delis :: Schematron -> XSLT Converter

1. Set /delis/delis-sch2xslt-converter current dir
2. Copy schematron file into /delis/delis-sch2xslt-converter/sch folder
3. Run mvn clean install
4. Look fo converted files in /delis/delis-sch2xslt-converter/xslt folder  
Note. Currently, /sch and /xslt contain example file "PEPPOLBIS-T111" - schematron and converted xslt.


# Sources of schematron files:

BIS3/CII CEN UBL GitHub project: https://github.com/CenPC434/validation

BIS3 CEN/PEPPOL UBL Schematron files: 

current version: http://docs.peppol.eu/poacc/upgrade-3/
next version: https://next-test-docs.peppol.eu/poacc/billing/3.0/

Another important source: https://github.com/OpenPEPPOL/peppol-bis-invoice-3/tree/master/rules/sch