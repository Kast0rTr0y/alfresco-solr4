package org.alfresco.solr.query;

public abstract class AbstractStructuredFieldPosition implements StructuredFieldPosition
{
    private String termText;

    private boolean isTerminal;

    private boolean isAbsolute;

    private CachingTermPositions tps;

    public AbstractStructuredFieldPosition(String termText, boolean isTerminal, boolean isAbsolute)
    {
        super();
        this.termText = termText;
        this.isTerminal = isTerminal;
        this.isAbsolute = isAbsolute;
    }

    public boolean isTerminal()
    {
        return isTerminal;
    }

    protected void setTerminal(boolean isTerminal)
    {
        this.isTerminal = isTerminal;
    }

    public boolean isAbsolute()
    {
        return isAbsolute;
    }

    public boolean isRelative()
    {
        return !isAbsolute;
    }

    public String getTermText()
    {
        return termText;
    }

    public int getPosition()
    {
        return -1;
    }

    public void setCachingTermPositions(CachingTermPositions tps)
    {
        this.tps = tps;
    }

    public CachingTermPositions getCachingTermPositions()
    {
        return this.tps;
    }

    
    
    public boolean allowsLinkingBySelf()
    {
       return false;
    }

    public boolean allowslinkingByParent()
    {
        return true;
    }

    public boolean linkParent()
    {
        return true;
    }

    public boolean linkSelf()
    {
       return false;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(256);
        buffer.append(getDescription());
        buffer.append("<"+getTermText()+"> at "+getPosition());
        buffer.append(" Terminal = "+isTerminal());
        buffer.append(" Absolute = "+isAbsolute());
        return buffer.toString();
    }
    
    public abstract String getDescription();

    public boolean isDescendant()
    {
        return false;
    }
    
    public boolean matchesAll()
    {
        return getCachingTermPositions() == null;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isAbsolute ? 1231 : 1237);
        result = prime * result + (isTerminal ? 1231 : 1237);
        result = prime * result + ((termText == null) ? 0 : termText.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractStructuredFieldPosition other = (AbstractStructuredFieldPosition) obj;
        if (isAbsolute != other.isAbsolute)
            return false;
        if (isTerminal != other.isTerminal)
            return false;
        if (termText == null)
        {
            if (other.termText != null)
                return false;
        }
        else if (!termText.equals(other.termText))
            return false;
        return true;
    }

 

    
}
