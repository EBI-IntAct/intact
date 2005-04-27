// $ANTLR 2.7.3: "iql2lucene.g" -> "Iql2LuceneParser.java"$

package uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.parser.iql2luceneParser;

import antlr.*;
import antlr.collections.AST;
import antlr.collections.impl.BitSet;

public class Iql2LuceneParser extends antlr.LLkParser implements Iql2LuceneParserTokenTypes {

    // the class where to search in
    String searchObject = null;
    // the database where to search in
    String database = null;
    // flag to controll if the IQL statement was right
    boolean isCorrectStatement = true;
    TokenStreamSelector selector;

    public void init(TokenStreamSelector selector) {
        this.selector = selector;
    }

    protected Iql2LuceneParser(TokenBuffer tokenBuf, int k) {
        super(tokenBuf, k);
        tokenNames = _tokenNames;
        buildTokenTypeASTClassMap();
        astFactory = new ASTFactory(getTokenTypeToASTClassMap());
    }

    public Iql2LuceneParser(TokenBuffer tokenBuf) {
        this(tokenBuf, 2);
    }

    protected Iql2LuceneParser(TokenStream lexer, int k) {
        super(lexer, k);
        tokenNames = _tokenNames;
        buildTokenTypeASTClassMap();
        astFactory = new ASTFactory(getTokenTypeToASTClassMap());
    }

    public Iql2LuceneParser(TokenStream lexer) {
        this(lexer, 2);
    }

    public Iql2LuceneParser(ParserSharedInputState state) {
        super(state, 2);
        tokenNames = _tokenNames;
        buildTokenTypeASTClassMap();
        astFactory = new ASTFactory(getTokenTypeToASTClassMap());
    }

    public final String statement() throws RecognitionException, TokenStreamException, ANTLRException {
        String searchObj;

        returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST statement_AST = null;
        searchObj = null;

        try {      // for error handling
            selectStatement();
            astFactory.addASTChild(currentAST, returnAST);
            {
                switch (LA(1)) {
                    case SEMICOLON:
                        {
                            match(SEMICOLON);
                            break;
                        }
                    case EOF:
                        {
                            break;
                        }
                    default:
                        {
                            throw new NoViableAltException(LT(1), getFilename());
                        }
                }
            }
            match(Token.EOF_TYPE);
            if (inputState.guessing == 0) {
                if (isCorrectStatement) {
                    searchObj = searchObject;
                } else {
                    throw new IllegalArgumentException("this was an invalid IQL statement");
                }

            }
            statement_AST = (AST) currentAST.root;
        } catch (ANTLRException ex) {
            if (inputState.guessing == 0) {

                reportError("in statement " + ex.toString());
                return null;

            } else {
                throw ex;
            }
        }
        returnAST = statement_AST;
        return searchObj;
    }

    public final void selectStatement() throws RecognitionException, TokenStreamException, ANTLRException {

        returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST selectStatement_AST = null;

        try {      // for error handling
            selectClause();
            {
                switch (LA(1)) {
                    case FROM:
                        {
                            fromClause();
                            break;
                        }
                    case EOF:
                    case SEMICOLON:
                    case WHERE:
                        {
                            break;
                        }
                    default:
                        {
                            throw new NoViableAltException(LT(1), getFilename());
                        }
                }
            }
            {
                switch (LA(1)) {
                    case WHERE:
                        {
                            whereClause();
                            astFactory.addASTChild(currentAST, returnAST);
                            break;
                        }
                    case EOF:
                    case SEMICOLON:
                        {
                            break;
                        }
                    default:
                        {
                            throw new NoViableAltException(LT(1), getFilename());
                        }
                }
            }
            selectStatement_AST = (AST) currentAST.root;
        } catch (ANTLRException ex) {
            if (inputState.guessing == 0) {

                reportError(ex.toString());
                isCorrectStatement = false;
                throw new ANTLRException("this was an invalid IQL statement");

            } else {
                throw ex;
            }
        }
        returnAST = selectStatement_AST;
    }

    public final void selectClause() throws RecognitionException, TokenStreamException, ANTLRException {

        returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST selectClause_AST = null;

        try {      // for error handling
            AST tmp3_AST = null;
            tmp3_AST = astFactory.create(LT(1));
            astFactory.addASTChild(currentAST, tmp3_AST);
            match(SELECT);
            searchObject();
            astFactory.addASTChild(currentAST, returnAST);
            selectClause_AST = (AST) currentAST.root;
        } catch (ANTLRException ex) {
            if (inputState.guessing == 0) {

                reportError(ex.toString());
                isCorrectStatement = false;
                throw new ANTLRException("this was an invalid IQL statement");

            } else {
                throw ex;
            }
        }
        returnAST = selectClause_AST;
    }

    public final void fromClause() throws RecognitionException, TokenStreamException, ANTLRException {

        returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST fromClause_AST = null;
        Token i = null;
        AST i_AST = null;

        try {      // for error handling
            AST tmp4_AST = null;
            tmp4_AST = astFactory.create(LT(1));
            astFactory.addASTChild(currentAST, tmp4_AST);
            match(FROM);
            i = LT(1);
            i_AST = astFactory.create(i);
            astFactory.addASTChild(currentAST, i_AST);
            match(INTACT);
            if (inputState.guessing == 0) {
                database = i.getText().toLowerCase();
            }
            fromClause_AST = (AST) currentAST.root;
        } catch (ANTLRException ex) {
            if (inputState.guessing == 0) {

                reportError(ex.toString());
                isCorrectStatement = false;
                throw new ANTLRException("this was an invalid IQL statement");

            } else {
                throw ex;
            }
        }
        returnAST = fromClause_AST;
    }

    public final void whereClause() throws RecognitionException, TokenStreamException, ANTLRException {

        returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST whereClause_AST = null;

        try {      // for error handling
            match(WHERE);
            searchCondition();
            astFactory.addASTChild(currentAST, returnAST);
            whereClause_AST = (AST) currentAST.root;
        } catch (ANTLRException ex) {
            if (inputState.guessing == 0) {

                reportError(ex.toString());
                isCorrectStatement = false;
                throw new ANTLRException("this was an invalid IQL statement");

            } else {
                throw ex;
            }
        }
        returnAST = whereClause_AST;
    }

    public final void searchObject() throws RecognitionException, TokenStreamException, ANTLRException {

        returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST searchObject_AST = null;
        Token p = null;
        AST p_AST = null;
        Token i = null;
        AST i_AST = null;
        Token e = null;
        AST e_AST = null;
        Token c = null;
        AST c_AST = null;
        Token a = null;
        AST a_AST = null;
        String str = "";

        try {      // for error handling
            {
                switch (LA(1)) {
                    case PROTEIN:
                        {
                            p = LT(1);
                            p_AST = astFactory.create(p);
                            astFactory.addASTChild(currentAST, p_AST);
                            match(PROTEIN);
                            if (inputState.guessing == 0) {
                                str = p.getText();
                            }
                            break;
                        }
                    case INTERACTION:
                        {
                            i = LT(1);
                            i_AST = astFactory.create(i);
                            astFactory.addASTChild(currentAST, i_AST);
                            match(INTERACTION);
                            if (inputState.guessing == 0) {
                                str = i.getText();
                            }
                            break;
                        }
                    case EXPERIMENT:
                        {
                            e = LT(1);
                            e_AST = astFactory.create(e);
                            astFactory.addASTChild(currentAST, e_AST);
                            match(EXPERIMENT);
                            if (inputState.guessing == 0) {
                                str = e.getText();
                            }
                            break;
                        }
                    case CV:
                        {
                            c = LT(1);
                            c_AST = astFactory.create(c);
                            astFactory.addASTChild(currentAST, c_AST);
                            match(CV);
                            if (inputState.guessing == 0) {
                                str = c.getText();
                            }
                            break;
                        }
                    case ANY:
                        {
                            a = LT(1);
                            a_AST = astFactory.create(a);
                            astFactory.addASTChild(currentAST, a_AST);
                            match(ANY);
                            if (inputState.guessing == 0) {
                                str = a.getText();
                            }
                            break;
                        }
                    default:
                        {
                            throw new NoViableAltException(LT(1), getFilename());
                        }
                }
            }
            if (inputState.guessing == 0) {
                if (!str.equals("")) {
                    str.toLowerCase();
                    searchObject = str;
                }

            }
            searchObject_AST = (AST) currentAST.root;
        } catch (ANTLRException ex) {
            if (inputState.guessing == 0) {

                reportError(ex.toString());
                isCorrectStatement = false;
                throw new ANTLRException("this was an invalid IQL statement");

            } else {
                throw ex;
            }
        }
        returnAST = searchObject_AST;
    }

    public final void searchCondition() throws RecognitionException, TokenStreamException, ANTLRException {

        returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST searchCondition_AST = null;

        try {      // for error handling
            criteria();
            astFactory.addASTChild(currentAST, returnAST);
            {
                _loop11:
                do {
                    if ((LA(1) == AND || LA(1) == OR)) {
                        {
                            switch (LA(1)) {
                                case AND:
                                    {
                                        AST tmp6_AST = null;
                                        tmp6_AST = astFactory.create(LT(1));
                                        astFactory.makeASTRoot(currentAST, tmp6_AST);
                                        match(AND);
                                        break;
                                    }
                                case OR:
                                    {
                                        AST tmp7_AST = null;
                                        tmp7_AST = astFactory.create(LT(1));
                                        astFactory.makeASTRoot(currentAST, tmp7_AST);
                                        match(OR);
                                        break;
                                    }
                                default:
                                    {
                                        throw new NoViableAltException(LT(1), getFilename());
                                    }
                            }
                        }
                        criteria();
                        astFactory.addASTChild(currentAST, returnAST);
                    } else {
                        break _loop11;
                    }

                } while (true);
            }
            searchCondition_AST = (AST) currentAST.root;
        } catch (ANTLRException ex) {
            if (inputState.guessing == 0) {

                reportError(ex.toString());
                isCorrectStatement = false;
                throw new ANTLRException("this was an invalid IQL statement");

            } else {
                throw ex;
            }
        }
        returnAST = searchCondition_AST;
    }

    public final void criteria() throws RecognitionException, TokenStreamException, ANTLRException {

        returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST criteria_AST = null;
        Token lp = null;
        AST lp_AST = null;
        Token rp = null;
        AST rp_AST = null;

        try {      // for error handling
            {
                switch (LA(1)) {
                    case LPAREN:
                        {
                            lp = LT(1);
                            lp_AST = astFactory.create(lp);
                            match(LPAREN);
                            searchCondition();
                            astFactory.addASTChild(currentAST, returnAST);
                            rp = LT(1);
                            rp_AST = astFactory.create(rp);
                            match(RPAREN);
                            break;
                        }
                    case AC:
                    case SHORTLABEL:
                    case FULLNAME:
                    case ALIAS:
                    case ANNOTATION:
                    case XREF:
                    case CVINTERACTION_AC:
                    case CVINTERACTION_SHORTLABEL:
                    case CVINTERACTION_FULLNAME:
                    case CVIDENTIFICATION_AC:
                    case CVIDENTIFICATION_SHORTLABEL:
                    case CVIDENTIFICATION_FULLNAME:
                    case CVINTERACTION_TYPE_AC:
                    case CVINTERACTION_TYPE_SHORTLABEL:
                    case CVINTERACTION_TYPE_FULLNAME:
                    case CABRI:
                    case FLYBASE:
                    case GO:
                    case HUGE:
                    case INTACT:
                    case INTERPRO:
                    case NEWT:
                    case OMIM:
                    case PDB:
                    case PSIMI:
                    case PUBMED:
                    case REACTOME:
                    case RESID:
                    case SGD:
                    case UNIPROT:
                    case AGONIST:
                    case ANTAGONIST:
                    case AUTHORCONFIDENCE:
                    case CAUTION:
                    case COMMENT:
                    case CONFIDENCEMAPPING:
                    case DEFINITION:
                    case DISEASE:
                    case EXAMPLE:
                    case EXPMODIFICATION:
                    case FUNCTION:
                    case INHIBITION:
                    case ISOFORMCOMMENT:
                    case KINETICS:
                    case NEGATIVE:
                    case ONHOLD:
                    case PATHWAY:
                    case PREREQUISITEPTM:
                    case REMARKINTERNAL:
                    case RESULTINGPTM:
                    case SEARCHURL:
                    case SEARCHURLASCII:
                    case STIMULATION:
                    case SUBMITTED:
                    case UNIPROTCCNOTE:
                    case UNIPROTDREXPORT:
                    case URL:
                    case GENENAME:
                    case GENENAMESYNONYM:
                    case GOSYNONYM:
                    case ISOFORMSYNONYM:
                    case LOCUSNAME:
                    case ORFNAME:
                        {
                            predicate();
                            astFactory.addASTChild(currentAST, returnAST);
                            break;
                        }
                    default:
                        {
                            throw new NoViableAltException(LT(1), getFilename());
                        }
                }
            }
            criteria_AST = (AST) currentAST.root;
        } catch (ANTLRException ex) {
            if (inputState.guessing == 0) {

                reportError(ex.toString());
                isCorrectStatement = false;
                throw new ANTLRException("this was an invalid IQL statement");

            } else {
                throw ex;
            }
        }
        returnAST = criteria_AST;
    }

    public final void predicate() throws RecognitionException, TokenStreamException, ANTLRException {

        returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST predicate_AST = null;

        try {      // for error handling
            {
                term();
                astFactory.addASTChild(currentAST, returnAST);
                {
                    switch (LA(1)) {
                        case EQUALS:
                            {
                                AST tmp8_AST = null;
                                tmp8_AST = astFactory.create(LT(1));
                                astFactory.makeASTRoot(currentAST, tmp8_AST);
                                match(EQUALS);
                                {
                                    value();
                                    astFactory.addASTChild(currentAST, returnAST);
                                }
                                break;
                            }
                        case LIKE:
                            {
                                AST tmp9_AST = null;
                                tmp9_AST = astFactory.create(LT(1));
                                astFactory.makeASTRoot(currentAST, tmp9_AST);
                                match(LIKE);
                                {
                                    value();
                                    astFactory.addASTChild(currentAST, returnAST);
                                }
                                break;
                            }
                        default:
                            {
                                throw new NoViableAltException(LT(1), getFilename());
                            }
                    }
                }
            }
            predicate_AST = (AST) currentAST.root;
        } catch (ANTLRException ex) {
            if (inputState.guessing == 0) {

                reportError(ex.toString());
                isCorrectStatement = false;
                throw new ANTLRException("this was an invalid IQL statement");

            } else {
                throw ex;
            }
        }
        returnAST = predicate_AST;
    }

    public final void term() throws RecognitionException, TokenStreamException, ANTLRException {

        returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST term_AST = null;
        String str = "";

        try {      // for error handling
            {
                switch (LA(1)) {
                    case AC:
                        {
                            AST tmp10_AST = null;
                            tmp10_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp10_AST);
                            match(AC);
                            break;
                        }
                    case SHORTLABEL:
                        {
                            AST tmp11_AST = null;
                            tmp11_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp11_AST);
                            match(SHORTLABEL);
                            break;
                        }
                    case FULLNAME:
                        {
                            AST tmp12_AST = null;
                            tmp12_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp12_AST);
                            match(FULLNAME);
                            break;
                        }
                    case ALIAS:
                        {
                            AST tmp13_AST = null;
                            tmp13_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp13_AST);
                            match(ALIAS);
                            break;
                        }
                    case ANNOTATION:
                        {
                            AST tmp14_AST = null;
                            tmp14_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp14_AST);
                            match(ANNOTATION);
                            break;
                        }
                    case XREF:
                        {
                            AST tmp15_AST = null;
                            tmp15_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp15_AST);
                            match(XREF);
                            break;
                        }
                    case CVINTERACTION_AC:
                        {
                            AST tmp16_AST = null;
                            tmp16_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp16_AST);
                            match(CVINTERACTION_AC);
                            break;
                        }
                    case CVINTERACTION_SHORTLABEL:
                        {
                            AST tmp17_AST = null;
                            tmp17_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp17_AST);
                            match(CVINTERACTION_SHORTLABEL);
                            break;
                        }
                    case CVINTERACTION_FULLNAME:
                        {
                            AST tmp18_AST = null;
                            tmp18_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp18_AST);
                            match(CVINTERACTION_FULLNAME);
                            break;
                        }
                    case CVIDENTIFICATION_AC:
                        {
                            AST tmp19_AST = null;
                            tmp19_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp19_AST);
                            match(CVIDENTIFICATION_AC);
                            break;
                        }
                    case CVIDENTIFICATION_SHORTLABEL:
                        {
                            AST tmp20_AST = null;
                            tmp20_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp20_AST);
                            match(CVIDENTIFICATION_SHORTLABEL);
                            break;
                        }
                    case CVIDENTIFICATION_FULLNAME:
                        {
                            AST tmp21_AST = null;
                            tmp21_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp21_AST);
                            match(CVIDENTIFICATION_FULLNAME);
                            break;
                        }
                    case CVINTERACTION_TYPE_AC:
                        {
                            AST tmp22_AST = null;
                            tmp22_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp22_AST);
                            match(CVINTERACTION_TYPE_AC);
                            break;
                        }
                    case CVINTERACTION_TYPE_SHORTLABEL:
                        {
                            AST tmp23_AST = null;
                            tmp23_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp23_AST);
                            match(CVINTERACTION_TYPE_SHORTLABEL);
                            break;
                        }
                    case CVINTERACTION_TYPE_FULLNAME:
                        {
                            AST tmp24_AST = null;
                            tmp24_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp24_AST);
                            match(CVINTERACTION_TYPE_FULLNAME);
                            break;
                        }
                    case CABRI:
                        {
                            AST tmp25_AST = null;
                            tmp25_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp25_AST);
                            match(CABRI);
                            break;
                        }
                    case FLYBASE:
                        {
                            AST tmp26_AST = null;
                            tmp26_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp26_AST);
                            match(FLYBASE);
                            break;
                        }
                    case GO:
                        {
                            AST tmp27_AST = null;
                            tmp27_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp27_AST);
                            match(GO);
                            break;
                        }
                    case HUGE:
                        {
                            AST tmp28_AST = null;
                            tmp28_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp28_AST);
                            match(HUGE);
                            break;
                        }
                    case INTACT:
                        {
                            AST tmp29_AST = null;
                            tmp29_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp29_AST);
                            match(INTACT);
                            break;
                        }
                    case INTERPRO:
                        {
                            AST tmp30_AST = null;
                            tmp30_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp30_AST);
                            match(INTERPRO);
                            break;
                        }
                    case NEWT:
                        {
                            AST tmp31_AST = null;
                            tmp31_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp31_AST);
                            match(NEWT);
                            break;
                        }
                    case OMIM:
                        {
                            AST tmp32_AST = null;
                            tmp32_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp32_AST);
                            match(OMIM);
                            break;
                        }
                    case PDB:
                        {
                            AST tmp33_AST = null;
                            tmp33_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp33_AST);
                            match(PDB);
                            break;
                        }
                    case PSIMI:
                        {
                            AST tmp34_AST = null;
                            tmp34_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp34_AST);
                            match(PSIMI);
                            break;
                        }
                    case PUBMED:
                        {
                            AST tmp35_AST = null;
                            tmp35_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp35_AST);
                            match(PUBMED);
                            break;
                        }
                    case REACTOME:
                        {
                            AST tmp36_AST = null;
                            tmp36_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp36_AST);
                            match(REACTOME);
                            break;
                        }
                    case RESID:
                        {
                            AST tmp37_AST = null;
                            tmp37_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp37_AST);
                            match(RESID);
                            break;
                        }
                    case SGD:
                        {
                            AST tmp38_AST = null;
                            tmp38_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp38_AST);
                            match(SGD);
                            break;
                        }
                    case UNIPROT:
                        {
                            AST tmp39_AST = null;
                            tmp39_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp39_AST);
                            match(UNIPROT);
                            break;
                        }
                    case AGONIST:
                        {
                            AST tmp40_AST = null;
                            tmp40_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp40_AST);
                            match(AGONIST);
                            break;
                        }
                    case ANTAGONIST:
                        {
                            AST tmp41_AST = null;
                            tmp41_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp41_AST);
                            match(ANTAGONIST);
                            break;
                        }
                    case AUTHORCONFIDENCE:
                        {
                            AST tmp42_AST = null;
                            tmp42_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp42_AST);
                            match(AUTHORCONFIDENCE);
                            break;
                        }
                    case CAUTION:
                        {
                            AST tmp43_AST = null;
                            tmp43_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp43_AST);
                            match(CAUTION);
                            break;
                        }
                    case COMMENT:
                        {
                            AST tmp44_AST = null;
                            tmp44_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp44_AST);
                            match(COMMENT);
                            break;
                        }
                    case CONFIDENCEMAPPING:
                        {
                            AST tmp45_AST = null;
                            tmp45_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp45_AST);
                            match(CONFIDENCEMAPPING);
                            break;
                        }
                    case DEFINITION:
                        {
                            AST tmp46_AST = null;
                            tmp46_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp46_AST);
                            match(DEFINITION);
                            break;
                        }
                    case DISEASE:
                        {
                            AST tmp47_AST = null;
                            tmp47_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp47_AST);
                            match(DISEASE);
                            break;
                        }
                    case EXAMPLE:
                        {
                            AST tmp48_AST = null;
                            tmp48_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp48_AST);
                            match(EXAMPLE);
                            break;
                        }
                    case EXPMODIFICATION:
                        {
                            AST tmp49_AST = null;
                            tmp49_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp49_AST);
                            match(EXPMODIFICATION);
                            break;
                        }
                    case FUNCTION:
                        {
                            AST tmp50_AST = null;
                            tmp50_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp50_AST);
                            match(FUNCTION);
                            break;
                        }
                    case INHIBITION:
                        {
                            AST tmp51_AST = null;
                            tmp51_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp51_AST);
                            match(INHIBITION);
                            break;
                        }
                    case ISOFORMCOMMENT:
                        {
                            AST tmp52_AST = null;
                            tmp52_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp52_AST);
                            match(ISOFORMCOMMENT);
                            break;
                        }
                    case KINETICS:
                        {
                            AST tmp53_AST = null;
                            tmp53_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp53_AST);
                            match(KINETICS);
                            break;
                        }
                    case NEGATIVE:
                        {
                            AST tmp54_AST = null;
                            tmp54_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp54_AST);
                            match(NEGATIVE);
                            break;
                        }
                    case ONHOLD:
                        {
                            AST tmp55_AST = null;
                            tmp55_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp55_AST);
                            match(ONHOLD);
                            break;
                        }
                    case PATHWAY:
                        {
                            AST tmp56_AST = null;
                            tmp56_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp56_AST);
                            match(PATHWAY);
                            break;
                        }
                    case PREREQUISITEPTM:
                        {
                            AST tmp57_AST = null;
                            tmp57_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp57_AST);
                            match(PREREQUISITEPTM);
                            break;
                        }
                    case REMARKINTERNAL:
                        {
                            AST tmp58_AST = null;
                            tmp58_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp58_AST);
                            match(REMARKINTERNAL);
                            break;
                        }
                    case RESULTINGPTM:
                        {
                            AST tmp59_AST = null;
                            tmp59_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp59_AST);
                            match(RESULTINGPTM);
                            break;
                        }
                    case SEARCHURL:
                        {
                            AST tmp60_AST = null;
                            tmp60_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp60_AST);
                            match(SEARCHURL);
                            break;
                        }
                    case SEARCHURLASCII:
                        {
                            AST tmp61_AST = null;
                            tmp61_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp61_AST);
                            match(SEARCHURLASCII);
                            break;
                        }
                    case STIMULATION:
                        {
                            AST tmp62_AST = null;
                            tmp62_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp62_AST);
                            match(STIMULATION);
                            break;
                        }
                    case SUBMITTED:
                        {
                            AST tmp63_AST = null;
                            tmp63_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp63_AST);
                            match(SUBMITTED);
                            break;
                        }
                    case UNIPROTCCNOTE:
                        {
                            AST tmp64_AST = null;
                            tmp64_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp64_AST);
                            match(UNIPROTCCNOTE);
                            break;
                        }
                    case UNIPROTDREXPORT:
                        {
                            AST tmp65_AST = null;
                            tmp65_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp65_AST);
                            match(UNIPROTDREXPORT);
                            break;
                        }
                    case URL:
                        {
                            AST tmp66_AST = null;
                            tmp66_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp66_AST);
                            match(URL);
                            break;
                        }
                    case GENENAME:
                        {
                            AST tmp67_AST = null;
                            tmp67_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp67_AST);
                            match(GENENAME);
                            break;
                        }
                    case GENENAMESYNONYM:
                        {
                            AST tmp68_AST = null;
                            tmp68_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp68_AST);
                            match(GENENAMESYNONYM);
                            break;
                        }
                    case GOSYNONYM:
                        {
                            AST tmp69_AST = null;
                            tmp69_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp69_AST);
                            match(GOSYNONYM);
                            break;
                        }
                    case ISOFORMSYNONYM:
                        {
                            AST tmp70_AST = null;
                            tmp70_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp70_AST);
                            match(ISOFORMSYNONYM);
                            break;
                        }
                    case LOCUSNAME:
                        {
                            AST tmp71_AST = null;
                            tmp71_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp71_AST);
                            match(LOCUSNAME);
                            break;
                        }
                    case ORFNAME:
                        {
                            AST tmp72_AST = null;
                            tmp72_AST = astFactory.create(LT(1));
                            astFactory.addASTChild(currentAST, tmp72_AST);
                            match(ORFNAME);
                            break;
                        }
                    default:
                        {
                            throw new NoViableAltException(LT(1), getFilename());
                        }
                }
            }
            if (inputState.guessing == 0) {
            }
            term_AST = (AST) currentAST.root;
        } catch (ANTLRException ex) {
            if (inputState.guessing == 0) {

                reportError(ex.toString());
                isCorrectStatement = false;
                throw new ANTLRException("this was an invalid IQL statement");

            } else {
                throw ex;
            }
        }
        returnAST = term_AST;
    }

    public final void value() throws RecognitionException, TokenStreamException, ANTLRException {

        returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST value_AST = null;
        Token v = null;
        AST v_AST = null;
        String val = "";

        try {      // for error handling
            {
                AST tmp73_AST = null;
                tmp73_AST = astFactory.create(LT(1));
                astFactory.addASTChild(currentAST, tmp73_AST);
                match(QUOTE);
                if (inputState.guessing == 0) {
                    selector.push("valuelexer");
                }
                v = LT(1);
                v_AST = astFactory.create(v);
                astFactory.addASTChild(currentAST, v_AST);
                match(VALUE);
                if (inputState.guessing == 0) {
                    /** todo in iql2lucene.g: is it right to convert like that? */
                    val = v.getText();

                }
            }
            if (inputState.guessing == 0) {
            }
            if (inputState.guessing == 0) {
                selector.pop();
            }
            AST tmp74_AST = null;
            tmp74_AST = astFactory.create(LT(1));
            astFactory.addASTChild(currentAST, tmp74_AST);
            match(QUOTE);
            value_AST = (AST) currentAST.root;
        } catch (RecognitionException ex) {
            if (inputState.guessing == 0) {
                reportError(ex);
                consume();
                consumeUntil(_tokenSet_0);
            } else {
                throw ex;
            }
        }
        returnAST = value_AST;
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

    protected void buildTokenTypeASTClassMap() {
        tokenTypeToASTClassMap = null;
    };

    private static final long[] mk_tokenSet_0() {
        long[] data = {45570L, 0L};
        return data;
    }

    public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());

}
