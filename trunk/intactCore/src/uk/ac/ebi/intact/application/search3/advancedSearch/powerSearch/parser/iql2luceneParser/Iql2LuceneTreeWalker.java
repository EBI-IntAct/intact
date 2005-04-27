// $ANTLR 2.7.3: "iql2lucene.g" -> "Iql2LuceneTreeWalker.java"$

package uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.parser.iql2luceneParser;

import antlr.NoViableAltException;
import antlr.RecognitionException;
import antlr.TokenStreamSelector;
import antlr.collections.AST;
import uk.ac.ebi.intact.util.SearchReplace;


public class Iql2LuceneTreeWalker extends antlr.TreeParser implements Iql2LuceneParserTokenTypes {


    TokenStreamSelector selector;

    // initialize the TokenStreamSelector
    public void init(TokenStreamSelector selector) {
        this.selector = selector;
    }

    public Iql2LuceneTreeWalker() {
        tokenNames = _tokenNames;
    }

    public final String criteria(AST _t) throws RecognitionException {
        String luceneStr;

        AST criteria_AST_in = (_t == ASTNULL) ? null : (AST) _t;

        luceneStr = null;
        String a = null;
        String b = null;


        try {      // for error handling
            if (_t == null) _t = ASTNULL;
            switch (_t.getType()) {
                case AND:
                    {
                        AST __t42 = _t;
                        AST tmp75_AST_in = (AST) _t;
                        match(_t, AND);
                        _t = _t.getFirstChild();
                        a = criteria(_t);
                        _t = _retTree;
                        b = criteria(_t);
                        _t = _retTree;
                        _t = __t42;
                        _t = _t.getNextSibling();
                        luceneStr = "(" + a + " AND " + b + ")";

                        break;
                    }
                case OR:
                    {
                        AST __t43 = _t;
                        AST tmp76_AST_in = (AST) _t;
                        match(_t, OR);
                        _t = _t.getFirstChild();
                        a = criteria(_t);
                        _t = _retTree;
                        b = criteria(_t);
                        _t = _retTree;
                        _t = __t43;
                        _t = _t.getNextSibling();
                        luceneStr = "(" + a + " OR " + b + ")";

                        break;
                    }
                case EQUALS:
                case LIKE:
                    {
                        luceneStr = predicate(_t);
                        _t = _retTree;
                        break;
                    }
                default:
                    {
                        throw new NoViableAltException(_t);
                    }
            }
        } catch (RecognitionException ex) {
            reportError(ex);
            if (_t != null) {
                _t = _t.getNextSibling();
            }
        }
        _retTree = _t;
        return luceneStr;
    }

    public final String predicate(AST _t) throws RecognitionException {
        String pred;

        AST predicate_AST_in = (_t == ASTNULL) ? null : (AST) _t;

        String t, v;
        pred = null;


        try {      // for error handling
            if (_t == null) _t = ASTNULL;
            switch (_t.getType()) {
                case LIKE:
                    {
                        AST __t45 = _t;
                        AST tmp77_AST_in = (AST) _t;
                        match(_t, LIKE);
                        _t = _t.getFirstChild();
                        t = term(_t);
                        _t = _retTree;
                        v = value(_t);
                        _t = _retTree;
                        _t = __t45;
                        _t = _t.getNextSibling();


                        pred = t + ":(" + v + ")";

                        break;
                    }
                case EQUALS:
                    {
                        AST __t46 = _t;
                        AST tmp78_AST_in = (AST) _t;
                        match(_t, EQUALS);
                        _t = _t.getFirstChild();
                        t = term(_t);
                        _t = _retTree;
                        v = value(_t);
                        _t = _retTree;
                        _t = __t46;
                        _t = _t.getNextSibling();

                        pred = t + ":(" + v + ")";

                        break;
                    }
                default:
                    {
                        throw new NoViableAltException(_t);
                    }
            }
        } catch (RecognitionException ex) {
            reportError(ex);
            if (_t != null) {
                _t = _t.getNextSibling();
            }
        }
        _retTree = _t;
        return pred;
    }

    public final String term(AST _t) throws RecognitionException {
        String str;

        AST term_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST a = null;
        AST s = null;
        AST d = null;
        AST al = null;
        AST ann = null;
        AST x = null;
        AST intac = null;
        AST intsh = null;
        AST intfull = null;
        AST idac = null;
        AST idsh = null;
        AST idfull = null;
        AST itac = null;
        AST itsh = null;
        AST itfull = null;
        AST ca = null;
        AST f = null;
        AST g = null;
        AST h = null;
        AST i = null;
        AST in = null;
        AST n = null;
        AST o = null;
        AST p = null;
        AST ps = null;
        AST pu = null;
        AST r = null;
        AST re = null;
        AST sg = null;
        AST u = null;
        AST ag = null;
        AST an = null;
        AST au = null;
        AST cau = null;
        AST co = null;
        AST con = null;
        AST de = null;
        AST di = null;
        AST exa = null;
        AST exp = null;
        AST fu = null;
        AST inh = null;
        AST iso = null;
        AST ki = null;
        AST ne = null;
        AST on = null;
        AST pa = null;
        AST pr = null;
        AST rem = null;
        AST res = null;
        AST se = null;
        AST sea = null;
        AST st = null;
        AST su = null;
        AST cc = null;
        AST dr = null;
        AST url = null;
        AST ge = null;
        AST gen = null;
        AST go = null;
        AST is = null;
        AST lo = null;
        AST or = null;
        str = "";

        try {      // for error handling
            {
                if (_t == null) _t = ASTNULL;
                switch (_t.getType()) {
                    case AC:
                        {
                            a = (AST) _t;
                            match(_t, AC);
                            _t = _t.getNextSibling();
                            str = a.getText();
                            break;
                        }
                    case SHORTLABEL:
                        {
                            s = (AST) _t;
                            match(_t, SHORTLABEL);
                            _t = _t.getNextSibling();
                            str = s.getText();
                            break;
                        }
                    case FULLNAME:
                        {
                            d = (AST) _t;
                            match(_t, FULLNAME);
                            _t = _t.getNextSibling();
                            str = d.getText();
                            break;
                        }
                    case ALIAS:
                        {
                            al = (AST) _t;
                            match(_t, ALIAS);
                            _t = _t.getNextSibling();
                            str = al.getText();
                            break;
                        }
                    case ANNOTATION:
                        {
                            ann = (AST) _t;
                            match(_t, ANNOTATION);
                            _t = _t.getNextSibling();
                            str = ann.getText();
                            break;
                        }
                    case XREF:
                        {
                            x = (AST) _t;
                            match(_t, XREF);
                            _t = _t.getNextSibling();
                            str = x.getText();
                            break;
                        }
                    case CVINTERACTION_AC:
                        {
                            intac = (AST) _t;
                            match(_t, CVINTERACTION_AC);
                            _t = _t.getNextSibling();
                            str = intac.getText();
                            break;
                        }
                    case CVINTERACTION_SHORTLABEL:
                        {
                            intsh = (AST) _t;
                            match(_t, CVINTERACTION_SHORTLABEL);
                            _t = _t.getNextSibling();
                            str = intsh.getText();
                            break;
                        }
                    case CVINTERACTION_FULLNAME:
                        {
                            intfull = (AST) _t;
                            match(_t, CVINTERACTION_FULLNAME);
                            _t = _t.getNextSibling();
                            str = intfull.getText();
                            break;
                        }
                    case CVIDENTIFICATION_AC:
                        {
                            idac = (AST) _t;
                            match(_t, CVIDENTIFICATION_AC);
                            _t = _t.getNextSibling();
                            str = idac.getText();
                            break;
                        }
                    case CVIDENTIFICATION_SHORTLABEL:
                        {
                            idsh = (AST) _t;
                            match(_t, CVIDENTIFICATION_SHORTLABEL);
                            _t = _t.getNextSibling();
                            str = idsh.getText();
                            break;
                        }
                    case CVIDENTIFICATION_FULLNAME:
                        {
                            idfull = (AST) _t;
                            match(_t, CVIDENTIFICATION_FULLNAME);
                            _t = _t.getNextSibling();
                            str = idfull.getText();
                            break;
                        }
                    case CVINTERACTION_TYPE_AC:
                        {
                            itac = (AST) _t;
                            match(_t, CVINTERACTION_TYPE_AC);
                            _t = _t.getNextSibling();
                            str = itac.getText();
                            break;
                        }
                    case CVINTERACTION_TYPE_SHORTLABEL:
                        {
                            itsh = (AST) _t;
                            match(_t, CVINTERACTION_TYPE_SHORTLABEL);
                            _t = _t.getNextSibling();
                            str = itsh.getText();
                            break;
                        }
                    case CVINTERACTION_TYPE_FULLNAME:
                        {
                            itfull = (AST) _t;
                            match(_t, CVINTERACTION_TYPE_FULLNAME);
                            _t = _t.getNextSibling();
                            str = itfull.getText();
                            break;
                        }
                    case CABRI:
                        {
                            ca = (AST) _t;
                            match(_t, CABRI);
                            _t = _t.getNextSibling();
                            str = ca.getText();
                            break;
                        }
                    case FLYBASE:
                        {
                            f = (AST) _t;
                            match(_t, FLYBASE);
                            _t = _t.getNextSibling();
                            str = f.getText();
                            break;
                        }
                    case GO:
                        {
                            g = (AST) _t;
                            match(_t, GO);
                            _t = _t.getNextSibling();
                            str = g.getText();
                            break;
                        }
                    case HUGE:
                        {
                            h = (AST) _t;
                            match(_t, HUGE);
                            _t = _t.getNextSibling();
                            str = h.getText();
                            break;
                        }
                    case INTACT:
                        {
                            i = (AST) _t;
                            match(_t, INTACT);
                            _t = _t.getNextSibling();
                            str = i.getText();
                            break;
                        }
                    case INTERPRO:
                        {
                            in = (AST) _t;
                            match(_t, INTERPRO);
                            _t = _t.getNextSibling();
                            str = in.getText();
                            break;
                        }
                    case NEWT:
                        {
                            n = (AST) _t;
                            match(_t, NEWT);
                            _t = _t.getNextSibling();
                            str = n.getText();
                            break;
                        }
                    case OMIM:
                        {
                            o = (AST) _t;
                            match(_t, OMIM);
                            _t = _t.getNextSibling();
                            str = o.getText();
                            break;
                        }
                    case PDB:
                        {
                            p = (AST) _t;
                            match(_t, PDB);
                            _t = _t.getNextSibling();
                            str = p.getText();
                            break;
                        }
                    case PSIMI:
                        {
                            ps = (AST) _t;
                            match(_t, PSIMI);
                            _t = _t.getNextSibling();
                            str = ps.getText();
                            break;
                        }
                    case PUBMED:
                        {
                            pu = (AST) _t;
                            match(_t, PUBMED);
                            _t = _t.getNextSibling();
                            str = pu.getText();
                            break;
                        }
                    case REACTOME:
                        {
                            r = (AST) _t;
                            match(_t, REACTOME);
                            _t = _t.getNextSibling();
                            str = r.getText();
                            break;
                        }
                    case RESID:
                        {
                            re = (AST) _t;
                            match(_t, RESID);
                            _t = _t.getNextSibling();
                            str = re.getText();
                            break;
                        }
                    case SGD:
                        {
                            sg = (AST) _t;
                            match(_t, SGD);
                            _t = _t.getNextSibling();
                            str = sg.getText();
                            break;
                        }
                    case UNIPROT:
                        {
                            u = (AST) _t;
                            match(_t, UNIPROT);
                            _t = _t.getNextSibling();
                            str = u.getText();
                            break;
                        }
                    case AGONIST:
                        {
                            ag = (AST) _t;
                            match(_t, AGONIST);
                            _t = _t.getNextSibling();
                            str = ag.getText();
                            break;
                        }
                    case ANTAGONIST:
                        {
                            an = (AST) _t;
                            match(_t, ANTAGONIST);
                            _t = _t.getNextSibling();
                            str = an.getText();
                            break;
                        }
                    case AUTHORCONFIDENCE:
                        {
                            au = (AST) _t;
                            match(_t, AUTHORCONFIDENCE);
                            _t = _t.getNextSibling();
                            str = au.getText();
                            break;
                        }
                    case CAUTION:
                        {
                            cau = (AST) _t;
                            match(_t, CAUTION);
                            _t = _t.getNextSibling();
                            str = cau.getText();
                            break;
                        }
                    case COMMENT:
                        {
                            co = (AST) _t;
                            match(_t, COMMENT);
                            _t = _t.getNextSibling();
                            str = co.getText();
                            break;
                        }
                    case CONFIDENCEMAPPING:
                        {
                            con = (AST) _t;
                            match(_t, CONFIDENCEMAPPING);
                            _t = _t.getNextSibling();
                            str = con.getText();
                            break;
                        }
                    case DEFINITION:
                        {
                            de = (AST) _t;
                            match(_t, DEFINITION);
                            _t = _t.getNextSibling();
                            str = de.getText();
                            break;
                        }
                    case DISEASE:
                        {
                            di = (AST) _t;
                            match(_t, DISEASE);
                            _t = _t.getNextSibling();
                            str = di.getText();
                            break;
                        }
                    case EXAMPLE:
                        {
                            exa = (AST) _t;
                            match(_t, EXAMPLE);
                            _t = _t.getNextSibling();
                            str = exa.getText();
                            break;
                        }
                    case EXPMODIFICATION:
                        {
                            exp = (AST) _t;
                            match(_t, EXPMODIFICATION);
                            _t = _t.getNextSibling();
                            str = exp.getText();
                            break;
                        }
                    case FUNCTION:
                        {
                            fu = (AST) _t;
                            match(_t, FUNCTION);
                            _t = _t.getNextSibling();
                            str = fu.getText();
                            break;
                        }
                    case INHIBITION:
                        {
                            inh = (AST) _t;
                            match(_t, INHIBITION);
                            _t = _t.getNextSibling();
                            str = inh.getText();
                            break;
                        }
                    case ISOFORMCOMMENT:
                        {
                            iso = (AST) _t;
                            match(_t, ISOFORMCOMMENT);
                            _t = _t.getNextSibling();
                            str = iso.getText();
                            break;
                        }
                    case KINETICS:
                        {
                            ki = (AST) _t;
                            match(_t, KINETICS);
                            _t = _t.getNextSibling();
                            str = ki.getText();
                            break;
                        }
                    case NEGATIVE:
                        {
                            ne = (AST) _t;
                            match(_t, NEGATIVE);
                            _t = _t.getNextSibling();
                            str = ne.getText();
                            break;
                        }
                    case ONHOLD:
                        {
                            on = (AST) _t;
                            match(_t, ONHOLD);
                            _t = _t.getNextSibling();
                            str = on.getText();
                            break;
                        }
                    case PATHWAY:
                        {
                            pa = (AST) _t;
                            match(_t, PATHWAY);
                            _t = _t.getNextSibling();
                            str = pa.getText();
                            break;
                        }
                    case PREREQUISITEPTM:
                        {
                            pr = (AST) _t;
                            match(_t, PREREQUISITEPTM);
                            _t = _t.getNextSibling();
                            str = pr.getText();
                            break;
                        }
                    case REMARKINTERNAL:
                        {
                            rem = (AST) _t;
                            match(_t, REMARKINTERNAL);
                            _t = _t.getNextSibling();
                            str = rem.getText();
                            break;
                        }
                    case RESULTINGPTM:
                        {
                            res = (AST) _t;
                            match(_t, RESULTINGPTM);
                            _t = _t.getNextSibling();
                            str = res.getText();
                            break;
                        }
                    case SEARCHURL:
                        {
                            se = (AST) _t;
                            match(_t, SEARCHURL);
                            _t = _t.getNextSibling();
                            str = se.getText();
                            break;
                        }
                    case SEARCHURLASCII:
                        {
                            sea = (AST) _t;
                            match(_t, SEARCHURLASCII);
                            _t = _t.getNextSibling();
                            str = sea.getText();
                            break;
                        }
                    case STIMULATION:
                        {
                            st = (AST) _t;
                            match(_t, STIMULATION);
                            _t = _t.getNextSibling();
                            str = st.getText();
                            break;
                        }
                    case SUBMITTED:
                        {
                            su = (AST) _t;
                            match(_t, SUBMITTED);
                            _t = _t.getNextSibling();
                            str = su.getText();
                            break;
                        }
                    case UNIPROTCCNOTE:
                        {
                            cc = (AST) _t;
                            match(_t, UNIPROTCCNOTE);
                            _t = _t.getNextSibling();
                            str = cc.getText();
                            break;
                        }
                    case UNIPROTDREXPORT:
                        {
                            dr = (AST) _t;
                            match(_t, UNIPROTDREXPORT);
                            _t = _t.getNextSibling();
                            str = dr.getText();
                            break;
                        }
                    case URL:
                        {
                            url = (AST) _t;
                            match(_t, URL);
                            _t = _t.getNextSibling();
                            str = url.getText();
                            break;
                        }
                    case GENENAME:
                        {
                            ge = (AST) _t;
                            match(_t, GENENAME);
                            _t = _t.getNextSibling();
                            str = ge.getText();
                            break;
                        }
                    case GENENAMESYNONYM:
                        {
                            gen = (AST) _t;
                            match(_t, GENENAMESYNONYM);
                            _t = _t.getNextSibling();
                            str = gen.getText();
                            break;
                        }
                    case GOSYNONYM:
                        {
                            go = (AST) _t;
                            match(_t, GOSYNONYM);
                            _t = _t.getNextSibling();
                            str = go.getText();
                            break;
                        }
                    case ISOFORMSYNONYM:
                        {
                            is = (AST) _t;
                            match(_t, ISOFORMSYNONYM);
                            _t = _t.getNextSibling();
                            str = is.getText();
                            break;
                        }
                    case LOCUSNAME:
                        {
                            lo = (AST) _t;
                            match(_t, LOCUSNAME);
                            _t = _t.getNextSibling();
                            str = lo.getText();
                            break;
                        }
                    case ORFNAME:
                        {
                            or = (AST) _t;
                            match(_t, ORFNAME);
                            _t = _t.getNextSibling();
                            str = or.getText();
                            break;
                        }
                    default:
                        {
                            throw new NoViableAltException(_t);
                        }
                }
            }
        } catch (RecognitionException ex) {
            reportError(ex);
            if (_t != null) {
                _t = _t.getNextSibling();
            }
        }
        _retTree = _t;
        return str;
    }

    public final String value(AST _t) throws RecognitionException {
        String val;

        AST value_AST_in = (_t == ASTNULL) ? null : (AST) _t;
        AST v = null;
        val = "";

        try {      // for error handling
            {
                AST tmp79_AST_in = (AST) _t;
                match(_t, QUOTE);
                _t = _t.getNextSibling();
                selector.push("valuelexer");
                v = (AST) _t;
                match(_t, VALUE);
                _t = _t.getNextSibling();
                val = v.getText();
                // escape some lucene syntax characters
                val = SearchReplace.replace(val, "\\\\", "\\\\\\\\");
                val = SearchReplace.replace(val, ":", "\\\\:");
                val = SearchReplace.replace(val, ")", "\\\\)");
                val = SearchReplace.replace(val, "(", "\\\\(");
                val = SearchReplace.replace(val, "+", "\\\\+");
                val = SearchReplace.replace(val, "-", "\\\\-");
                val = SearchReplace.replace(val, "!", "\\\\!");
                val = SearchReplace.replace(val, "&&", "\\\\&&");
                val = SearchReplace.replace(val, "||", "\\\\||");

            }
            selector.pop();
            AST tmp80_AST_in = (AST) _t;
            match(_t, QUOTE);
            _t = _t.getNextSibling();
        } catch (RecognitionException ex) {
            reportError(ex);
            if (_t != null) {
                _t = _t.getNextSibling();
            }
        }
        _retTree = _t;
        return val;
    }


    public static final String[] _tokenNames = {
        "<0>",
        "EOF",
        "<2>",
        "NULL_TREE_LOOKAHEAD",
        "Whitespace",
        "Letter",
        "Digit",
        "SpecialChar",
        "VALUE",
        "SEMICOLON",
        "\"select\"",
        "\"where\"",
        "\"and\"",
        "\"or\"",
        "LPAREN",
        "RPAREN",
        "EQUALS",
        "\"like\"",
        "\"ac\"",
        "\"shortlabel\"",
        "\"fullname\"",
        "\"alias\"",
        "\"annotation\"",
        "\"xref\"",
        "\"interaction_ac\"",
        "\"interaction_shortlabel\"",
        "\"interaction_fullname\"",
        "\"identification_ac\"",
        "\"identification_shortlabel\"",
        "\"identification_fullname\"",
        "\"interactiontype_ac\"",
        "\"interactiontype_shortlabel\"",
        "\"interactiontype_fullname\"",
        "\"cabri\"",
        "\"flybase\"",
        "\"go\"",
        "\"huge\"",
        "\"intact\"",
        "\"interpro\"",
        "\"newt\"",
        "\"omim\"",
        "\"pdb\"",
        "\"psi-mi\"",
        "\"pubmed\"",
        "\"reactome\"",
        "\"resid\"",
        "\"sgd\"",
        "\"uniprot\"",
        "\"agonist\"",
        "\"antagonist\"",
        "\"author-confidence\"",
        "\"caution\"",
        "\"comment\"",
        "\"confidence-mapping\"",
        "\"definition\"",
        "\"disease\"",
        "\"example\"",
        "\"exp-modification\"",
        "\"function\"",
        "\"inhibition\"",
        "\"isoform-comment\"",
        "\"kinetics\"",
        "\"negative\"",
        "\"onhold\"",
        "\"pathway\"",
        "\"prerequisite-ptm\"",
        "\"remark-internal\"",
        "\"resulting-ptm\"",
        "\"search-url\"",
        "\"search-url-ascii\"",
        "\"stimulation\"",
        "\"submitted\"",
        "\"uniprot-cc-note\"",
        "\"uniprot-dr-export\"",
        "\"url\"",
        "\"gene-name\"",
        "\"gene-name-synonym\"",
        "\"go-synonym\"",
        "\"isoform-synonym\"",
        "\"locus-name\"",
        "\"orf-name\"",
        "QUOTE",
        "\"protein\"",
        "\"interaction\"",
        "\"experiment\"",
        "\"cv\"",
        "\"any\"",
        "\"from\"",
        "\"database\"",
        "Identifier"
    };

}
	
