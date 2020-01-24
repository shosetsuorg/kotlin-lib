-- {"id":-1,"version":"9.9.9","author":"","repo":""}
---@author Doomsdayrs
---@version 1.0.0

Formatter = {
    identification = -1,
    baseURL = "", imageURL = "", name = "",
    isIncrementingChapterList = false, isIncrementingPassagePage = false, hasCloudFlare = false, hasSearch = true, hasGenres = true
}

function Formatter:new(o, baseURL, identification, imageURL, name)
    o = o or {}
    setmetatable(o, self)
    self.baseURL = baseURL
    self.identification = identification
    self.imageURL = imageURL
    self.name = name
    return self
end

---@param elements Elements
---@param f fun(v:Element):any
---@return table|Array
function Formatter:map(elements, f)
    local t = {}
    for i = 1, elements:size() do
        t[i] = f(elements:get(i - 1))
    end
    return t
end

---@param el Elements
---@param f1 fun(element:Element):Elements|Array|table
---@param f2 fun(v:Element):table|Array
function Formatter:map2flat(el, f1, f2)
    local t = {}
    local i = 1
    for j = 1, el:size() do
        local o2 = f1(el:get(j - 1))
        if o2 then
            for k = 1, o2:size() do
                t[i] = f2(o2:get(k - 1))
                i = i + 1
            end
        end
    end
    return t
end

function Formatter:getName()
    return self.name
end

function Formatter:getBaseURL()
    return self.baseURL
end

function Formatter:getImageURL()
    return self.imageURL
end

function Formatter:getID()
    return self.identification
end

function Formatter:isIncrementingChapterList()
    return self.isIncrementingChapterList
end

function Formatter:isIncrementingPassagePage()
    return self.isIncrementingPassagePage
end

function Formatter:hasCloudFlare()
    return self.hasCloudFlare
end

function Formatter:hasSearch()
    return self.hasSearch
end

function Formatter:hasGenres()
    return self.hasGenres
end

---@return Ordering
function Formatter:chapterOrder()
    return Ordering(0)
end

---@return Ordering
function Formatter:latestOrder()
    return Ordering(0)
end

---@return Array @Array<Genre>
function Formatter:genres()
    -- TODO Complete
    return {}
end

---@param page number @value
---@return string @url of said latest page
function Formatter:getLatestURL(page)
    error("TODO:Not Implemented")
end

---@param document Document @Jsoup document of the page with chapter text on it
---@return string @passage of chapter, If nothing can be parsed, then the text should describe why there isn't a chapter
function Formatter:getNovelPassage(document)
    error("TODO:Not Implemented")
end

---@param document Document @Jsoup document of the novel information page
---@return NovelPage
function Formatter:parseNovel(document)
    error("TODO:Not Implemented")
end

---@param document Document @Jsoup document of the novel information page
---@param increment number @Page #
---@return NovelPage
function Formatter:parseNovelI(document, increment)
    error("TODO:Not Implemented")
end

---@param url string @url of novel page
---@param increment number @which page
--- If this is not implemented, it will just return the url
function Formatter:novelPageCombiner(url, increment)
    return url
end

---@param document Document @Jsoup document of latest listing
---@return Array @Novel array list
function Formatter:parseLatest(document)
    error("TODO:Not Implemented")
end

---@param document Document @Jsoup document of search results
---@return Array @Novel array list
function Formatter:parseSearch(document)
    error("TODO:Not Implemented")
end

---@param query string @query to use
---@return string @url
function Formatter:getSearchString(query)
    error("TODO:Not Implemented")
end

function get()
    return Formatter:new(nil, nil, nil, nil, nil)
end

function Formatter:test()
    print("This is DEFAULT")
end

