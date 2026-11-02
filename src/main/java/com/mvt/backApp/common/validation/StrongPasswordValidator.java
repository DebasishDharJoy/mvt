package com.mvt.backApp.common.validation;
import jakarta.validation.*;import java.util.regex.Pattern;
public class StrongPasswordValidator implements ConstraintValidator<StrongPassword,String>{
 private static final Pattern U=Pattern.compile("[A-Z]"),L=Pattern.compile("[a-z]"),D=Pattern.compile("\\d"),S=Pattern.compile("[^A-Za-z0-9\\s]");
 public boolean isValid(String v,ConstraintValidatorContext c){return v==null||v.isBlank()||(U.matcher(v).find()&&L.matcher(v).find()&&D.matcher(v).find()&&S.matcher(v).find()&&v.chars().noneMatch(Character::isWhitespace));}}
