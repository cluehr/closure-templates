/*
 * Copyright 2020 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.template.soy.types.ast;

import com.google.auto.value.AutoValue;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.template.soy.base.SourceLocation;

/** Node representing a template type, e.g. () => html. */
@AutoValue
public abstract class TemplateTypeNode extends TypeNode {

  public static TemplateTypeNode create(
      SourceLocation sourceLocation, Iterable<Argument> arguments, TypeNode returnType) {
    return new AutoValue_TemplateTypeNode(
        sourceLocation, ImmutableList.copyOf(arguments), returnType);
  }

  /** A single named, typed argument to a template. */
  @AutoValue
  public abstract static class Argument {
    public static Argument create(SourceLocation nameLocation, String name, TypeNode type) {
      return new AutoValue_TemplateTypeNode_Argument(nameLocation, name, type);
    }

    public abstract SourceLocation nameLocation();

    public abstract String name();

    public abstract TypeNode type();

    @Override
    public final String toString() {
      return name() + ": " + type();
    }

    Argument copy() {
      return create(nameLocation(), name(), type().copy());
    }
  }

  public abstract ImmutableList<Argument> arguments();

  public abstract TypeNode returnType();

  @Override
  public final String toString() {
    if (arguments().size() < 3) {
      return "(" + Joiner.on(", ").join(arguments()) + ") => " + returnType();
    }
    return "(\n  " + Joiner.on(",\n  ").join(arguments()) + "\n) => " + returnType();
  }

  @Override
  public <T> T accept(TypeNodeVisitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public TemplateTypeNode copy() {
    ImmutableList.Builder<Argument> newArguments = ImmutableList.builder();
    for (Argument argument : arguments()) {
      newArguments.add(argument.copy());
    }
    TemplateTypeNode copy = create(sourceLocation(), newArguments.build(), returnType().copy());
    copy.copyResolvedTypeFrom(this);
    return copy;
  }
}
