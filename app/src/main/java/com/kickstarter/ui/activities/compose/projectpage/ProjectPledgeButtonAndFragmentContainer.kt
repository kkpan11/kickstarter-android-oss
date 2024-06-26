package com.kickstarter.ui.activities.compose.projectpage

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kickstarter.R
import com.kickstarter.libs.Environment
import com.kickstarter.libs.utils.RewardUtils
import com.kickstarter.libs.utils.RewardViewUtils
import com.kickstarter.libs.utils.extensions.isNullOrZero
import com.kickstarter.models.Project
import com.kickstarter.models.Reward
import com.kickstarter.models.ShippingRule
import com.kickstarter.models.StoredCard
import com.kickstarter.ui.activities.DisclaimerItems
import com.kickstarter.ui.compose.designsystem.KSPrimaryGreenButton
import com.kickstarter.ui.compose.designsystem.KSTheme
import com.kickstarter.ui.compose.designsystem.KSTheme.colors
import com.kickstarter.ui.compose.designsystem.KSTheme.dimensions
import com.kickstarter.ui.data.PledgeReason
import com.kickstarter.ui.toolbars.compose.TopToolBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ProjectPledgeButtonAndContainerPreview() {
    KSTheme {
        var expanded by remember {
            mutableStateOf(false)
        }
        val pagerState = rememberPagerState(initialPage = 1, pageCount = { 4 })

        val coroutineScope = rememberCoroutineScope()
        ProjectPledgeButtonAndFragmentContainer(
            expanded = expanded,
            onContinueClicked = { expanded = !expanded },
            onBackClicked = {
                if (pagerState.currentPage > 1) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            page = pagerState.currentPage - 1,
                            animationSpec = tween(
                                durationMillis = 150,
                                easing = FastOutSlowInEasing
                            )
                        )
                    }
                } else {
                    expanded = !expanded
                }
            },
            isLoading = false,
            pagerState = pagerState,
            onAddOnsContinueClicked = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(
                        page = 2,
                        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
                    )
                }
            },
            environment = Environment.builder().build(),
            rewardsList = listOf(),
            addOns = listOf(),
            project = Project.builder().build(),
            onRewardSelected = {},
            onAddOnAddedOrRemoved = {},
            selectedAddOnsMap = mapOf(),
            totalAmount = 0.0,
            shippingSelectorIsGone = false,
            currentShippingRule = ShippingRule.builder().build(),
            onShippingRuleSelected = {},
            onConfirmDetailsContinueClicked = {},
            selectedRewardAndAddOnList = listOf(),
            onBonusSupportMinusClicked = {},
            onBonusSupportPlusClicked = {},
            onBonusSupportInputted = {},
            storedCards = listOf(),
            userEmail = "test@test.test",
            onPledgeCtaClicked = {},
            onAddPaymentMethodClicked = {},
            onDisclaimerItemClicked = {},
            onAccountabilityLinkClicked = {}
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProjectPledgeButtonAndFragmentContainer(
    expanded: Boolean,
    onContinueClicked: () -> Unit,
    onBackClicked: () -> Unit,
    pagerState: PagerState,
    isLoading: Boolean,
    onAddOnsContinueClicked: () -> Unit,
    shippingSelectorIsGone: Boolean,
    shippingRules: List<ShippingRule> = listOf(),
    currentShippingRule: ShippingRule,
    environment: Environment?,
    initialRewardCarouselPosition: Int = 0,
    rewardsList: List<Reward>,
    addOns: List<Reward>,
    project: Project,
    onRewardSelected: (reward: Reward) -> Unit,
    onAddOnAddedOrRemoved: (Map<Reward, Int>) -> Unit,
    selectedAddOnsMap: Map<Reward, Int>,
    totalAmount: Double,
    selectedReward: Reward? = null,
    onShippingRuleSelected: (ShippingRule) -> Unit,
    initialBonusSupportAmount: Double = 0.0,
    totalBonusSupportAmount: Double = 0.0,
    maxPledgeAmount: Double = 0.0,
    minStepAmount: Double = 0.0,
    onConfirmDetailsContinueClicked: () -> Unit,
    shippingAmount: Double = 0.0,
    selectedRewardAndAddOnList: List<Reward>,
    onBonusSupportPlusClicked: () -> Unit,
    onBonusSupportMinusClicked: () -> Unit,
    onBonusSupportInputted: (input: Double) -> Unit,
    storedCards: List<StoredCard>,
    userEmail: String,
    onPledgeCtaClicked: (selectedCard: StoredCard?) -> Unit,
    onAddPaymentMethodClicked: () -> Unit,
    onDisclaimerItemClicked: (disclaimerItem: DisclaimerItems) -> Unit,
    onAccountabilityLinkClicked: () -> Unit
) {
    Column {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = if (expanded) {
                RectangleShape
            } else {
                RoundedCornerShape(
                    topStart = dimensions.radiusLarge,
                    topEnd = dimensions.radiusLarge
                )
            },
            color = colors.backgroundSurfacePrimary,
            elevation = dimensions.elevationLarge,
        ) {
            AnimatedVisibility(
                visible = !expanded,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = 350,
                        easing = FastOutSlowInEasing
                    )
                ),
                exit = fadeOut(
                    animationSpec = tween(
                        durationMillis = 150,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensions.paddingMediumLarge)
                ) {
                    KSPrimaryGreenButton(
                        onClickAction = onContinueClicked,
                        text = stringResource(id = R.string.Back_this_project),
                        isEnabled = true
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(
                    expandFrom = Alignment.Bottom,
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = FastOutSlowInEasing
                    )
                ),
                exit = shrinkVertically(
                    shrinkTowards = Alignment.Bottom,
                    animationSpec = tween(
                        durationMillis = 350,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
                Scaffold(
                    topBar = {
                        TopToolBar(
                            title = stringResource(id = R.string.Back_this_project),
                            titleColor = colors.textPrimary,
                            leftOnClickAction = onBackClicked,
                            leftIconColor = colors.textPrimary,
                            backgroundColor = colors.backgroundSurfacePrimary,
                        )
                    }
                ) { padding ->
                    Box(
                        Modifier
                            .padding(padding)
                            .fillMaxSize()
                    ) {
                        HorizontalPager(
                            userScrollEnabled = false,
                            state = pagerState
                        ) { page ->
                            when (page) {
                                0 -> {
                                    RewardCarouselScreen(
                                        modifier = Modifier,
                                        lazyRowState = rememberLazyListState(
                                            initialFirstVisibleItemIndex = initialRewardCarouselPosition
                                        ),
                                        environment = environment ?: Environment.builder().build(),
                                        rewards = rewardsList,
                                        project = project,
                                        onRewardSelected = onRewardSelected,
                                        isLoading = isLoading
                                    )
                                }

                                1 -> {
                                    AddOnsScreen(
                                        modifier = Modifier,
                                        environment = environment ?: Environment.builder().build(),
                                        lazyColumnListState = rememberLazyListState(),
                                        countryList = shippingRules,
                                        shippingSelectorIsGone = shippingSelectorIsGone,
                                        currentShippingRule = currentShippingRule,
                                        onShippingRuleSelected = onShippingRuleSelected,
                                        rewardItems = addOns,
                                        project = project,
                                        onItemAddedOrRemoved = onAddOnAddedOrRemoved,
                                        selectedAddOnsMap = selectedAddOnsMap,
                                        onContinueClicked = onAddOnsContinueClicked,
                                        isLoading = isLoading,
                                    )
                                }

                                2 -> {
                                    ConfirmPledgeDetailsScreen(
                                        modifier = Modifier,
                                        environment = environment ?: Environment.builder().build(),
                                        project = project,
                                        selectedReward = selectedReward,
                                        onContinueClicked = onConfirmDetailsContinueClicked,
                                        onShippingRuleSelected = onShippingRuleSelected,
                                        totalAmount = totalAmount,
                                        shippingAmount = shippingAmount,
                                        currentShippingRule = currentShippingRule,
                                        countryList = shippingRules,
                                        initialBonusSupport = initialBonusSupportAmount,
                                        totalBonusSupport = totalBonusSupportAmount,
                                        maxPledgeAmount = maxPledgeAmount,
                                        minPledgeStep = minStepAmount,
                                        rewardsList = getRewardListAndPrices(
                                            selectedRewardAndAddOnList, environment, project
                                        ),
                                        rewardsContainAddOns = selectedRewardAndAddOnList.any { it.isAddOn() },
                                        rewardsHaveShippables = selectedRewardAndAddOnList.any {
                                            RewardUtils.isShippable(it)
                                        },
                                        onBonusSupportPlusClicked = onBonusSupportPlusClicked,
                                        onBonusSupportMinusClicked = onBonusSupportMinusClicked,
                                        onBonusSupportInputted = onBonusSupportInputted,
                                        isLoading = isLoading,
                                    )
                                }

                                3 -> {
                                    CheckoutScreen(
                                        storedCards = storedCards,
                                        environment = environment ?: Environment.builder().build(),
                                        ksString = environment?.ksString(),
                                        project = project,
                                        email = userEmail,
                                        selectedReward = selectedReward,
                                        rewardsList = getRewardListAndPrices(
                                            selectedRewardAndAddOnList,
                                            environment,
                                            project
                                        ),
                                        pledgeReason = PledgeReason.PLEDGE,
                                        shippingAmount = shippingAmount,
                                        totalAmount = totalAmount,
                                        totalBonusSupport = totalBonusSupportAmount,
                                        currentShippingRule = currentShippingRule,
                                        rewardsHaveShippables = selectedRewardAndAddOnList.any {
                                            RewardUtils.isShippable(it)
                                        },
                                        onPledgeCtaClicked = onPledgeCtaClicked,
                                        newPaymentMethodClicked = onAddPaymentMethodClicked,
                                        isLoading = isLoading,
                                        onDisclaimerItemClicked = onDisclaimerItemClicked,
                                        onAccountabilityLinkClicked = onAccountabilityLinkClicked
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getRewardListAndPrices(
    rewardsList: List<Reward>,
    environment: Environment?,
    project: Project
): List<Pair<String, String>> {
    return rewardsList.map { reward ->
        if (!reward.quantity().isNullOrZero()) {
            val title = reward.title() ?: ""
            val quantity = reward.quantity() ?: 1
            Pair(
                "$title X $quantity",
                environment?.ksCurrency()?.let {
                    RewardViewUtils.styleCurrency(
                        reward.minimum() * quantity,
                        project,
                        it
                    ).toString()
                } ?: ""
            )
        } else {
            Pair(
                reward.title() ?: "",
                environment?.ksCurrency()?.let {
                    RewardViewUtils.styleCurrency(
                        reward.minimum(),
                        project,
                        it
                    ).toString()
                } ?: ""
            )
        }
    }
}
