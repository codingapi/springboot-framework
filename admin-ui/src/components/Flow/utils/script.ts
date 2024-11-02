

class GroovyScript {

    public static defaultOutTrigger = "def run(content) {return true;}";
    public static defaultTitleGenerator = "def run(content){ return content.getCurrentOperator().getName() + '-' + content.getFlowWork().getTitle() + '-' + content.getFlowNode().getName();}";

    public static anyOperatorMatcher="def run(content) {return [content.getCurrentOperator().getUserId()];}";
    public static creatorOperatorMatcher="def run(content) {return [content.getCreateOperator().getUserId()];}";
    public static specifyOperatorMatcher="def run(content) {return [%s];}";


    static operatorMatcherType(operatorMatcher: string) {
        if (operatorMatcher === GroovyScript.anyOperatorMatcher) {
            return "any";
        } else if (operatorMatcher === GroovyScript.creatorOperatorMatcher) {
            return "creator";
        } else {
            return "custom";
        }
    }

    static errTriggerType(errTrigger: string) {
        if (errTrigger === GroovyScript.defaultOutTrigger) {
            return "default";
        } else {
            return "custom";
        }
    }

    static titleGeneratorType = (titleGenerator: string) => {
        if (titleGenerator === GroovyScript.defaultTitleGenerator) {
            return "default";
        } else {
            return "custom";
        }
    }

    static operatorMatcher(operatorMatcherType: string) {
        if (operatorMatcherType === "any") {
            return GroovyScript.anyOperatorMatcher;
        } else if (operatorMatcherType === "creator") {
            return GroovyScript.creatorOperatorMatcher;
        } else {
            return GroovyScript.specifyOperatorMatcher;
        }
    }
}

export default GroovyScript;
