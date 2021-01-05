![SolrBrsPlugin Logo](assets/Apache_Solr_Logo.svg)

SolrBrsPlugin
========

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

SolrBrsPlugin is a fork of [SolrSwan](https://github.com/o19s/SolrSwan) changed for Portuguese operators.

SolrBrsPlugin is a query parser and highlighter for Solr that accepts proximity and Boolean queries. The syntax is designed to be compatible with the search syntax of BRS Search. In addition to a changed fielded query syntax, the additional operators are:

* MESMO
* COM
* ADJ
* PROX
* XOU
* E
* OU
* NAO

Each operator takes an optional quantifier, such as "ADJ3", that restricts the range over which it operates. In order to make sense of paragraphs and sentences, the searched fields need to index special tokens that identify the breaks. The test schema has an example of how to do that with solr.PatternReplaceCharFilterFactory.

The included highlighter is a modified version of the FastVectorHighlighter that is currently the default in Solr. It supports semantically accurate, multi-colored highlighting. (The Phrase highlighter in Solr supports the first, while FVH supports the second, but neither support both).

# Building
SolrBrsPlugin uses Maven dependency management and Java 7. Once those are installed, building the plugin JAR is done with:
```
mvn package
```
This will build both the plugin jar as well as a webapp that can be used to parse queries without executing them. Both packages will be in the ./target folder.

# Installation
There are a few steps needed to get Swan syntax working in Solr. First, add the new jars to solrconfig.xml like:
```xml
  <lib path="../../apache-solr-8.7.3/contrib/SolrBrsPlugin-1.0-SNAPSHOT.jar" />
  <lib path="../../apache-solr-8.7.3/contrib/parboiled-core-1.1.8.jar" />
  <lib path="../../apache-solr-8.7.3/contrib/parboiled-java-1.1.8.jar" />
  <lib path="../../apache-solr-8.7.3/contrib/asm-all-5.2.jar" />
```
Then define the new query parser:
```xml
  <queryParser name="brs_qp" class="brs.components.BrsQParserPlugin">
    <str name="fieldAliases">fieldAliases.txt</str>
  </queryParser>

```
And then use the new parser in a request handler:
```xml
  <requestHandler name="/brs" class="solr.SearchHandler" default="true">
    <lst name="defaults">
      <str name="defType">brs_qp</str>
      <str name="sm">xxxsentencexxx</str>
      <str name="pm">xxxparagraphxxx</str>
      <str name="df">text_html</str>
```

The sm, pm, and df parameters are required. sm and pm are the sentence and paragraph markers you'll need to insert with an analyzer, and df can be whatever you want your default search field to be.

Next, in schema.xml you'll need to insert a couple charFilters into the analysis chain for whatever fields you want to use with SolrBrsPlugin:
```xml
    <!-- HTML based fields. -->
    <fieldType name="html" class="solr.TextField" positionIncrementGap="100">
      <analyzer type="index">
        <!-- find p h1 h2 h3 h4 h5 elements and add in paragraph token -->
        <charFilter class="solr.PatternReplaceCharFilterFactory"
                    pattern="^|(&lt;(?:[pP]|[hH]\d)&gt;)"
                    replacement="$1 xxxparagraphxxx xxxsentencexxx "/>
        <!-- find pattern "[sentence ending punctuation][space][Cap letter or number]
             and replace punctuation with sentence token -->
        <charFilter class="solr.PatternReplaceCharFilterFactory"
                    pattern="[.!?]\s+([A-Z0-9])"
                    replacement=" xxxsentencexxx $1"/>
        <charFilter class="solr.HTMLStripCharFilterFactory"/>
        <tokenizer class="solr.StandardTokenizerFactory"/>
        <filter class="solr.LowerCaseFilterFactory" />
      </analyzer>
      <analyzer type="query">
        <tokenizer class="solr.StandardTokenizerFactory"/>
        <filter class="solr.LowerCaseFilterFactory" />
      </analyzer>
    </fieldType>
```
This inserts the paragraph and sentence markers based on matching regular expressions. I'm not really sure what would happen if you only did sentence markers over a plain text field. The two possibilities are MESMO and COM operators would treat everything as if it was in the same paragraph, or they would never identify a paragraph in which to match.

Lastly, you need to create a text file that maps "dot" field aliases to the actual fields in your schema. For example, the following tells the parser to treat "ab" or "AB" as aliases for the abstract_html schema field:
```ruby
ab,AB => abstract_html
```
This will translate "device.ab." as a query for the term "device" over the abstract_html field. I don't remember at the moment whether or not the Swan field is still case-sensitive. Our whole fieldAliases.txt file looks like this:
```ruby
rev => revisor
rel => relator
iteo, inte => inteiro_teor
sigc => sigla_classe
clas => classe
```
Put that file alongside schema.xml and solrconfig.xml.

# BRS Operators

Op | Description
---- | -----------
ADJ | TermA next to TermB in the order specified in the same sentence
ADJ[n] | Two terms must occur within [n] terms of each other, in order, and within the same sentence.
PROX | TermA next to TermB in any order in the same sentence
PROX[n] | TermA within [n] words of TermB, in any order within the same sentence
COM | TermA in the same sentence with TermB
COM[n] | TermA within [n] sentences of TermB
MESMO | TermA in the same paragraph with TermB
MESMO[n] | TermA within [n] paragraphs of TermB
XOU | TermA OR TermB, but not both
OU | TermA OR TermB
E | TermA AND TermB
NAO | Not Term
