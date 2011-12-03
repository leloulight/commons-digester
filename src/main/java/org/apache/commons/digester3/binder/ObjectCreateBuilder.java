package org.apache.commons.digester3.binder;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import static java.lang.String.format;

import java.util.Arrays;

import org.apache.commons.digester3.ObjectCreateRule;

/**
 * Builder chained when invoking {@link LinkedRuleBuilder#createObject()}.
 *
 * @since 3.0
 */
public final class ObjectCreateBuilder
    extends AbstractBackToLinkedRuleBuilder<ObjectCreateRule>
{

    private final ClassLoader classLoader;

    private Class<?> type;

    private String attributeName;

    /**
     * The constructor arguments - order is preserved by the LinkedHashMap
     *
     * @since 3.2
     */
    private Class<?>[] constructorArgumentsType;

    ObjectCreateBuilder( String keyPattern, String namespaceURI, RulesBinder mainBinder, LinkedRuleBuilder mainBuilder,
                         ClassLoader classLoader )
    {
        super( keyPattern, namespaceURI, mainBinder, mainBuilder );
        this.classLoader = classLoader;
    }

    /**
     * Construct an object with the specified class name.
     *
     * @param className Java class name of the object to be created
     * @return this builder instance
     */
    public ObjectCreateBuilder ofType( String className )
    {
        if ( className == null )
        {
            reportError( "createObject().ofType( String )", "NULL Java type not allowed" );
            return this;
        }

        try
        {
            return ofType( this.classLoader.loadClass( className ) );
        }
        catch ( ClassNotFoundException e )
        {
            reportError( "createObject().ofType( String )", String.format( "class '%s' cannot be load", className ) );
            return this;
        }
    }

    /**
     * Construct an object with the specified class.
     *
     * @param <T> any java type
     * @param type Java class of the object to be created
     * @return this builder instance
     */
    public <T> ObjectCreateBuilder ofType( Class<T> type )
    {
        if ( type == null )
        {
            reportError( "createObject().ofType( Class<?> )", "NULL Java type not allowed" );
            return this;
        }

        this.type = type;

        return this;
    }

    /**
     * Allows specify the attribute containing an override class name if it is present.
     *
     * @param attributeName The attribute containing an override class name if it is present
     * @return this builder instance
     */
    public ObjectCreateBuilder ofTypeSpecifiedByAttribute( /* @Nullable */String attributeName )
    {
        this.attributeName = attributeName;
        return this;
    }

    /**
     *
     * @return
     * @since 3.2
     */
    public ObjectCreateBuilder usingConstructor( String...paramTypeNames )
    {
        if ( paramTypeNames == null )
        {
            reportError( "createObject().usingConstructor( String[] )", "NULL parametersTypes not allowed" );
        }

        Class<?>[] paramTypes = null;
        if ( paramTypeNames != null )
        {
            paramTypes = new Class<?>[paramTypeNames.length];
            for ( int i = 0; i < paramTypeNames.length; i++ )
            {
                try
                {
                    paramTypes[i] = classLoader.loadClass( paramTypeNames[i] );
                }
                catch ( ClassNotFoundException e )
                {
                    this.reportError( format( "createObject().usingConstructor( %s )",
                                              Arrays.toString( paramTypeNames ) ),
                                      format( "class '%s' cannot be load", paramTypeNames[i] ) );
                }
            }
        }

        return usingConstructor( paramTypes );
    }

    /**
     *
     * @return
     * @since 3.2
     */
    public ObjectCreateBuilder usingConstructor( Class<?>...constructorArgumentsType )
    {
        if ( constructorArgumentsType == null )
        {
            reportError( "createObject().usingConstructor( Class<?>[] )", "NULL parametersTypes not allowed" );
        }

        this.constructorArgumentsType = constructorArgumentsType;

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ObjectCreateRule createRule()
    {
        ObjectCreateRule objectCreateRule = new ObjectCreateRule( attributeName, type );

        if ( constructorArgumentsType != null )
        {
            objectCreateRule.setConstructorArguments( constructorArgumentsType );
        }

        return objectCreateRule;
    }

}
