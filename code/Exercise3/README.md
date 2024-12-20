# Code of your exercise

Put here all the code created for this exercise

Answer:

<?xml version="1.0"?>
<ruleset name="My Custom Rules"
xmlns="http://pmd.sourceforge.net/ruleset/2.0.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://pmd.sourceforge.net/ruleset/2.0.0
https://pmd.github.io/pmd-6.0.0/ruleset_2_0_0.xsd"
language="java">

    <rule name="NestedIfDepth3"
          message="Avoid using three or more nested if statements."
          class="net.sourceforge.pmd.lang.rule.XPathRule"
          language="java">
        <description>
            This rule triggers when an if statement is nested three or more levels deep.
        </description>
        <priority>3</priority>
        <properties>
            <property name="version" value="2.0"/>
        </properties>
        <expression>
            //IfStatement[descendant::IfStatement[descendant::IfStatement]]
        </expression>
    </rule>
</ruleset>
