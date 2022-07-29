package hr.mmaracic.rulepr.service;

import hr.mmaracic.rulepr.model.domain.*;
import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.mvel.DrlDumper;
import org.kie.api.builder.KieFileSystem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DrlMappingService {

    public List<BasicRule> simplifyRule(KieFileSystem kieFileSystem, BasicRule basicRule) {
        if (basicRule instanceof ReplacementRule replacementRule) {
            ReplaceExpression replaceExpression = replacementRule.getReplaceExpression();

            List<CompositeExpression> disableCompositeException = new ArrayList<>(replacementRule.getCompositeExpression());
            disableCompositeException.add(
                    new CompositeExpression().binaryLogicalOperator(BinaryLogicalOperator.AND).expression(
                            new Expression().attribute(replaceExpression.getAttribute()).unaryLogicalOperator(replaceExpression.getUnaryLogicalOperator()).addConditionsItem(
                                    new Condition().comparisonOperator(ComparisonOperator.EQUAL).valueType(replaceExpression.getOldValueType()).value(replaceExpression.getOldValue()))
                    )
            );
            FilteringRule disableRule = new FilteringRule()
                    .priority(replacementRule.getPriority()).action("Disable")
                    .expression(basicRule.getExpression())
                    .compositeExpression(disableCompositeException);

            List<CompositeExpression> enableCompositeException = new ArrayList<>(replacementRule.getCompositeExpression());
            enableCompositeException.add(
                    new CompositeExpression().binaryLogicalOperator(BinaryLogicalOperator.AND).expression(
                            new Expression().attribute(replaceExpression.getAttribute()).unaryLogicalOperator(replaceExpression.getUnaryLogicalOperator()).addConditionsItem(
                                    new Condition().comparisonOperator(ComparisonOperator.EQUAL).valueType(replaceExpression.getNewValueType()).value(replaceExpression.getNewValue()))
                    )
            );
            FilteringRule enableRule = new FilteringRule()
                    .priority(replacementRule.getPriority()).action("Enable")
                    .expression(basicRule.getExpression())
                    .compositeExpression(enableCompositeException);
            return List.of(disableRule, enableRule);
        } else {
            return List.of(basicRule);
        }
    }

    public void addRule(KieFileSystem kieFileSystem, List<BasicRule> basicRules) {
        PackageDescrBuilder packageDescrBuilder = DescrFactory.newPackage().name("com.example.model");
        basicRules.stream().forEach(basicRule ->
                packageDescrBuilder.newRule()
                        .name("Is of valid age")
                        .lhs()
                        .pattern("Person").constraint("age < 18").end()
                        .pattern().id("$a", false).type("Action").end()
                        .end()
                        .rhs("$a.showBanner( false );")
        );
        packageDescrBuilder.end();
        String rules = new DrlDumper().dump(packageDescrBuilder.getDescr());
        kieFileSystem.write("src/main/resources/rule-1.drl", rules);
    }
}