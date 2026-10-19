package com.example.breakapp.main.search

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breakapp.R
import com.example.breakapp.main.components.BreakHeader
import com.example.breakapp.main.data.MenuRepository
import com.example.breakapp.main.data.ProductMatch
import com.example.breakapp.main.data.RecentSearches
import com.example.breakapp.main.data.Restaurant
import com.example.breakapp.main.data.SearchResults
import com.example.breakapp.main.data.menuRepository
import com.example.breakapp.main.data.toCopPrice
import com.example.breakapp.main.theme.BreakColors
import com.example.breakapp.main.theme.BreakFonts

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(
    autoFocus: Boolean = false,
    onRestaurantClick: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    repository: MenuRepository = menuRepository
) {
    var query by rememberSaveable { mutableStateOf("") }
    var notFound by rememberSaveable { mutableStateOf(false) }
    var focused by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    val results = remember(query) { repository.search(query) }
    val expanded = focused && !notFound

    LaunchedEffect(Unit) {
        if (autoFocus) focusRequester.requestFocus()
    }

    BackHandler(enabled = expanded) { focusManager.clearFocus() }

    fun submit() {
        val term = query.trim()
        if (term.isEmpty()) return
        if (results.isEmpty) {
            notFound = true
            focusManager.clearFocus()
        } else {
            RecentSearches.add(term)
        }
        keyboard?.hide()
    }

    fun pick(term: String, restaurantId: String? = null) {
        query = term
        notFound = repository.search(term).isEmpty
        if (!notFound) RecentSearches.add(term)
        focusManager.clearFocus()
        keyboard?.hide()

        // Si la coincidencia incluye el ID del restaurante, navegamos a él directamente
        if (!restaurantId.isNullOrBlank()) {
            onRestaurantClick(restaurantId)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BreakColors.Background)
            .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } }
    ) {
        BreakHeader()

        SearchCard(
            query = query,
            onQueryChange = {
                query = it
                notFound = false
            },
            expanded = expanded,
            notFound = notFound,
            results = results,
            recents = RecentSearches.items,
            focusRequester = focusRequester,
            onFocusChange = { focused = it },
            onSubmit = { submit() },
            onClear = {
                query = ""
                notFound = false
                focusRequester.requestFocus()
            },
            onPickProduct = { match ->
                pick(match.product.name, match.restaurant.id)
            },
            onPickRestaurant = { restaurant ->
                pick(restaurant.name, restaurant.id)
            },
            onPickRecent = { term ->
                pick(term)
            }
        )

        when {
            notFound -> NotFoundContent(query = query)
            !expanded -> Mascot()
        }
    }
}

@Composable
private fun SearchCard(
    query: String,
    onQueryChange: (String) -> Unit,
    expanded: Boolean,
    notFound: Boolean,
    results: SearchResults,
    recents: List<String>,
    focusRequester: FocusRequester,
    onFocusChange: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    onClear: () -> Unit,
    onPickProduct: (ProductMatch) -> Unit,
    onPickRestaurant: (Restaurant) -> Unit,
    onPickRecent: (String) -> Unit
) {
    val shape = RoundedCornerShape(if (expanded || notFound) 34.dp else 20.dp)
    val rowHeight = when {
        notFound -> 53.dp
        expanded -> 44.dp
        else -> 38.dp
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 30.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(Color.White)
            .border(4.dp, BreakColors.Purple, shape)
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight)
                .padding(start = 10.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.main_ic_search_purple),
                contentDescription = "Buscar",
                modifier = Modifier.size(width = 24.dp, height = 23.dp)
            )
            Spacer(Modifier.width(15.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .onFocusChanged { onFocusChange(it.isFocused) },
                singleLine = true,
                textStyle = TextStyle(
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                ),
                cursorBrush = SolidColor(BreakColors.Purple),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (query.isEmpty()) {
                            Text(
                                text = "Buscar restaurantes o platos...",
                                fontFamily = BreakFonts.Poppins,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = Color.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        innerTextField()
                    }
                }
            )
            if (query.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onClear),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "x",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = BreakColors.Gray
                    )
                }
            }
        }

        if (expanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BreakColors.Divider)
            )
            Column(
                modifier = Modifier
                    .heightIn(max = 380.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 8.dp)
            ) {
                if (query.isNotBlank()) {
                    SectionLabel("Coincidencias")
                    if (results.isEmpty) {
                        Text(
                            text = "Sin coincidencias todavía",
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp),
                            fontFamily = BreakFonts.Inter,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = BreakColors.GrayLight
                        )
                    }
                    results.products.forEach { match: ProductMatch ->
                        MatchRow(
                            iconRes = R.drawable.main_img_search_bowl,
                            title = match.product.name,
                            subtitle = "${match.restaurant.name} - ${match.product.price.toCopPrice()}",
                            onClick = { onPickProduct(match) }
                        )
                    }
                    results.restaurants.forEach { restaurant: Restaurant ->
                        MatchRow(
                            iconRes = restaurant.logoRes,
                            title = restaurant.name,
                            subtitle = "Restaurante - ${restaurant.timeRange}",
                            onClick = { onPickRestaurant(restaurant) },
                            logo = true
                        )
                    }
                }
                if (recents.isNotEmpty()) {
                    SectionLabel("Recientes")
                    recents.forEach { term ->
                        RecentRow(term = term, onClick = { onPickRecent(term) })
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 18.dp, top = 12.dp, bottom = 4.dp),
        fontFamily = BreakFonts.Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = BreakColors.GrayLight
    )
}

@Composable
private fun MatchRow(
    @DrawableRes iconRes: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    logo: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            contentScale = if (logo) ContentScale.Fit else ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .padding(if (logo) 4.dp else 0.dp)
        )
        Spacer(Modifier.width(2.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = BreakColors.GrayLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun RecentRow(term: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.main_img_search_recent),
            contentDescription = null,
            modifier = Modifier.size(37.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = term,
            fontFamily = BreakFonts.Inter,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = BreakColors.Gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun Mascot() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 111.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.main_img_search_mascot),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp)
                .fillMaxWidth(0.865f)
                .aspectRatio(1000f / 750f)
        )
    }
}

@Composable
private fun NotFoundContent(query: String) {
    val shown = query.trim().replaceFirstChar { it.uppercase() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 42.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.main_img_search_empty),
            contentDescription = null,
            modifier = Modifier.size(width = 129.dp, height = 127.dp)
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "No encontramos “$shown”",
            fontFamily = BreakFonts.Inter,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Prueba con otro nombre o mira lo mejor calificado",
            modifier = Modifier.widthIn(max = 262.dp),
            fontFamily = BreakFonts.Inter,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}